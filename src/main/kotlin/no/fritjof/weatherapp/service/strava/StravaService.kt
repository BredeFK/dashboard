package no.fritjof.weatherapp.service.strava

import no.fritjof.weatherapp.dto.StravaActivityDto
import no.fritjof.weatherapp.model.Athlete
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@Service
class StravaService(
    @Qualifier("stravaWebClient") private val webClient: WebClient,
    @Value($$"${strava.client-id}") private val stravaClientId: String,
    @Value($$"${strava.client-secret}") private val stravaClientSecret: String,
    @Value($$"${strava.refresh-token}") private val stravaRefreshToken: String,
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

        // TODO : Fix this
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
        val clubId = 1248911
        val token = getAuthToken()

        return webClient.get()
            .uri {
                it.path("/clubs/$clubId/activities")
                    .queryParam("after", "1751241600")
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