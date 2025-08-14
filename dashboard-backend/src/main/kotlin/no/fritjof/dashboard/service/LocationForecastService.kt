package no.fritjof.dashboard.service

import no.fritjof.dashboard.dto.LocationForecastDto
import no.fritjof.dashboard.model.WeatherForecast
import no.fritjof.dashboard.model.WeatherInstance
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.TimeZone

@Service
class LocationForecastService(
    @Qualifier("locationForecastWebClient") private val webClient: WebClient,
    private val nominatimService: NominatimService,
) {

    private fun getComplete(latitude: Double, longitude: Double): LocationForecastDto? {
        return webClient.get()
            .uri {
                it.path("/complete")
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .build()
            }
            .retrieve()
            .bodyToMono(LocationForecastDto::class.java)
            .block()
    }

    @Cacheable("weatherForecast", key = "#latitude.toString() + #longitude.toString()")
    fun getWeatherForecast(latitude: Double, longitude: Double): WeatherForecast {
        val properties = getComplete(latitude, longitude)?.properties
        val weatherSeries: List<WeatherInstance>? = properties?.timeSeries?.filter {
            val timeStamp = toLocalDateTime(it.time)
            timeStamp.isAfter(LocalDateTime.now().minusHours(1))
                    && timeStamp.isBefore(LocalDateTime.now().plusHours(12))
        }?.map {
            WeatherInstance.toWeatherInstance(it, toLocalDateTime(it.time))
        }

        val locationName = nominatimService.getLocationName(latitude, longitude)
        return WeatherForecast(
            lastUpdated = toLocalDateTime(properties?.meta?.updatedAt),
            locationName = locationName,
            weatherSeries = weatherSeries ?: emptyList()
        )
    }

    private fun toLocalDateTime(datetime: String?): LocalDateTime {
        if (datetime == null) {
            throw NullPointerException("datetime is null")
        }
        return OffsetDateTime.parse(datetime)
            .atZoneSameInstant(TimeZone.getTimeZone("ECT").toZoneId())
            .toLocalDateTime()
    }
}
