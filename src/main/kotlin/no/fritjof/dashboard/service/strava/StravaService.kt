package no.fritjof.dashboard.service.strava

import no.fritjof.dashboard.dto.StravaActivityDto
import no.fritjof.dashboard.model.Athlete
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.TimeZone

@Service
class StravaService(
    @Qualifier("stravaWebClient") private val webClient: WebClient,
    @Value($$"${strava.client-id}") private val stravaClientId: String,
    @Value($$"${strava.client-secret}") private val stravaClientSecret: String,
    @Value($$"${strava.refresh-token}") private val stravaRefreshToken: String,
    @Value($$"${strava.club-id}") private val clubId: String,
    private val stravaTokenService: StravaTokenStore
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private fun getAuthToken(): String {
        val tokenStore = stravaTokenService.get()
        if (tokenStore != null && !tokenStore.hasExpired()) {
            logger.info("Token has not expired")
            return tokenStore.accessToken
        }

        val refreshToken = tokenStore?.refreshToken ?: stravaRefreshToken
        logger.info("Token has expired. Fetching a new one using refreshToken")

        val response = webClient.post()
            .uri {
                it.path("/oauth/token")
                    .queryParam("client_id", stravaClientId)
                    .queryParam("client_secret", stravaClientSecret)
                    .queryParam("refresh_token", refreshToken)
                    .queryParam("grant_type", "refresh_token")
                    .build()
            }
            .retrieve()
            .bodyToMono(StravaToken::class.java)
            .doOnError {
                logger.error("Error calling Strava API: ${it.message}", it)
            }
            .block()

        if (response?.accessToken != null) {
            stravaTokenService.save(response)
            logger.info("Strava token updated")
            return response.accessToken
        }

        throw RuntimeException("Could not get Strava Access token")
    }

    private fun getActivities(): List<StravaActivityDto>? {
        val token = getAuthToken()
        val thisWeeksMonday = getThisWeeksMondayInEpoch()

        return webClient.get()
            .uri {
                it.path("/clubs/$clubId/activities")
                    .queryParam("after", thisWeeksMonday)
                    .queryParam("sport_type", "Run")
                    .build()
            }
            .header("Authorization", "Bearer $token")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<StravaActivityDto>>() {})
            .doOnError {
                if (it is WebClientResponseException && it.statusCode == HttpStatus.valueOf(429)) {
                    logger.error("Reached limit of requests: ${it.message}")
                } else {
                    logger.error("Error while fetching strava activities: ${it.message}", it.cause)
                }
            }
            .block()
    }

    private fun getThisWeeksMondayInEpoch(): Long {
        val timeZoneId = TimeZone.getTimeZone("ECT").toZoneId()
        return LocalDate
            .now(timeZoneId)
            .with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
            )
            .atStartOfDay(timeZoneId)
            .toEpochSecond()
    }

    fun getScoreBoard(): List<Athlete> {
        val activities = getActivities()

        val athletes = mutableMapOf<String, Athlete>()

        activities?.forEach { activity ->
            athletes.getOrPut(activity.fullName()) {
                Athlete(activity.fullName())
            }.addActivity(
                movingTime = activity.movingTime,
                elevationGain = activity.totalElevationGain,
                distance = activity.distance
            )
        }

        if (athletes.isEmpty()) {
            return emptyList()
        }

        val sortedAthletes = athletes.values.sortedByDescending { it.totalDistance }
        sortedAthletes.forEach {
            it.setAveragePacePerKm()
        }

        return sortedAthletes
    }
}