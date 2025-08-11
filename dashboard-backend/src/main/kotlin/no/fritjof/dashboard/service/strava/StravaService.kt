package no.fritjof.dashboard.service.strava

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.google.gson.Gson
import no.fritjof.dashboard.dto.StravaActivityDto
import no.fritjof.dashboard.model.Athlete
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.ssl.SslProperties
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.io.File
import java.nio.charset.StandardCharsets
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters


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

    fun getScoreBoard(mock: Boolean): List<Athlete> {
        if (mock) {
            val gson = Gson()
            logger.info("Returning mock leaderboard")

            val resourceStream = Thread.currentThread().contextClassLoader
                .getResourceAsStream("strava-mock-file.json")
                ?: throw RuntimeException("Mock file not found on classpath")

            val json = resourceStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
            return gson.fromJson(json, Array<Athlete>::class.java).toList()

        }

        val thisMonday = getThisWeeksMondayUTC()
        val activities = getClubActivities(after = thisMonday)

        return convertActivitiesToListOfAthletes(activities)
    }

    fun getLastWeeksScoreBoard(): List<Athlete> {
        val (monday, sunday) = getLastMondayAndSundayUTC()

        val activitiesAfterLastWeeksMonday = getClubActivities(after = monday)
        val activitiesAfterLastWeeksSunday = getClubActivities(after = sunday)

        if (activitiesAfterLastWeeksMonday == null || activitiesAfterLastWeeksSunday == null) {
            throw IllegalStateException("Something went wrong with getting activities from last week")
        }

        val lastWeeksActivities = activitiesAfterLastWeeksMonday.filter { activity ->
            activity !in activitiesAfterLastWeeksSunday
        }

        return convertActivitiesToListOfAthletes(lastWeeksActivities)
    }

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

    private fun getClubActivities(after: ZonedDateTime): List<StravaActivityDto>? {
        val token = getAuthToken()
        logger.info("Getting activities after ${formatDate(after.toLocalDateTime())}: In Epoch Seconds ${after.toEpochSecond()}")
        return webClient.get()
            .uri {
                it.path("/clubs/$clubId/activities")
                    .queryParam("after", after.toEpochSecond())
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

    private fun formatDate(date: LocalDateTime): String {
        return "${date.dayOfWeek} ${date.dayOfMonth.toString().padStart(2, '0')}/${
            date.monthValue.toString().padStart(2, '0')
        }/${date.year} ${date.hour.toString().padStart(2, '0')}:${
            date.minute.toString().padStart(2, '0')
        }:${date.second.toString().padStart(2, '0')}"
    }

    private fun getThisWeeksMondayUTC(): ZonedDateTime {
        return ZonedDateTime.now(ZoneOffset.UTC)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .truncatedTo(ChronoUnit.DAYS) // Sets time to 00:00
    }

    private fun getLastMondayAndSundayUTC(): Pair<ZonedDateTime, ZonedDateTime> {
        val now = ZonedDateTime.now(ZoneOffset.UTC)

        val lastMonday = now
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .minusWeeks(1)
            .truncatedTo(ChronoUnit.DAYS)

        val lastSunday = lastMonday
            .plusDays(6)
            .withHour(23)
            .withMinute(59)
            .withSecond(59)

        return Pair(lastMonday, lastSunday)
    }


    private fun convertActivitiesToListOfAthletes(activities: List<StravaActivityDto>?): List<Athlete> {
        if (activities.isNullOrEmpty()) {
            logger.warn("List of activities is empty")
            return emptyList()
        }

        val athletes = mutableMapOf<String, Athlete>()
        activities.forEach { activity ->
            athletes.getOrPut(activity.fullName()) {
                Athlete(activity.fullName())
            }.addActivity(
                movingTime = activity.movingTime,
                elevationGain = activity.totalElevationGain.toInt(),
                distance = activity.distance
            )
        }

        if (athletes.isEmpty()) {
            logger.warn("List of athletes is empty")
            return emptyList()
        }

        val sortedAthletes = athletes.values.sortedByDescending { it.totalDistance }
        sortedAthletes.forEach {
            it.setAveragePacePerKm()
        }
        return sortedAthletes
    }
}
