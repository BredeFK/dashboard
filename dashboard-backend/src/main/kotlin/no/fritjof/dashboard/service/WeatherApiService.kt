package no.fritjof.dashboard.service

import no.fritjof.dashboard.dto.LocationForecastDto
import no.fritjof.dashboard.model.ImmediateWeatherForecast
import no.fritjof.dashboard.model.WeatherForecast
import no.fritjof.dashboard.model.WeatherInstance
import no.fritjof.dashboard.service.entur.EnTurService
import no.fritjof.dashboard.util.DateUtil.toLocalDateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@Service
class WeatherApiService(
    @Qualifier("weatherApiClient") private val webClient: WebClient,
    @Value($$"${weather-api.forcast-path}") private val forecastPath: String,
    @Value($$"${weather-api.nowcast-path}") private val nowcastPath: String,
    private val enTurService: EnTurService,
) {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    /* TODO : Uncomment for debugging or remove when done
    val places = listOf(
        Coordinates(latitude = 60.394, longitude = 5.325, name = "Bergen"),
        Coordinates(latitude = 59.757, 10.009, name = "Krokstadelva, Drammen"),
        Coordinates(latitude = 59.71941056890567, 10.144987193522217, "Konnerud, Drammen"),
        Coordinates(latitude = 59.942649978565186, 10.812822652854248, "Bjerke, Oslo"),
        Coordinates(latitude = 59.910465672466046, 10.765861538473102, "Iterate"),
    )
     */

    private fun getComplete(latitude: Double, longitude: Double): LocationForecastDto? {
        return webClient.get()
            .uri {
                it.path(forecastPath)
                    .path("/complete")
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .build()
            }
            .retrieve()
            .bodyToMono(LocationForecastDto::class.java)
            .block()
    }

    private fun getNowcast(latitude: Double, longitude: Double): LocationForecastDto? {
        return webClient.get()
            .uri {
                it.path(nowcastPath)
                    .path("/complete")
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .build()
            }
            .retrieve()
            .bodyToMono(LocationForecastDto::class.java)
            .block()
    }

    @Cacheable("immediateWeatherForecastCache", key = "#latitude.toString() + #longitude.toString()")
    fun getImmediateWeatherForecast(latitude: Double, longitude: Double): ImmediateWeatherForecast? {
        val forecast = getNowcast(latitude, longitude)
        if (forecast == null) {
            log.error("Could not get immediate weather forecast for: $latitude,$longitude")
            return null
        }
        return ImmediateWeatherForecast.toDto(forecast)
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

        /* TODO : Uncomment for debugging or remove when done
        val temp = mutableMapOf<Coordinates, String>()
        for (place in places) {
            temp[place] = enTurService.searchCoordinates(place.latitude, place.longitude)
        }

        println()
        temp.forEach { (place, locationName) ->
            println("${place.name} -> $locationName")
        }
        println()
         */

        var locationName = "Ukjent plass"
        try {
            locationName = enTurService.searchCoordinates(latitude, longitude)
        } catch (ex: Exception) {
            log.error("Could not get name from coordinates: $latitude,$longitude", ex)
        }

        return WeatherForecast(
            lastUpdated = toLocalDateTime(properties?.meta?.updatedAt),
            locationName = locationName,
            weatherSeries = weatherSeries ?: emptyList()
        )
    }
}
