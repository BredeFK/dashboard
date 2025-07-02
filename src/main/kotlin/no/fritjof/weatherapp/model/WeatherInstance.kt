package no.fritjof.weatherapp.model

import no.fritjof.weatherapp.dto.TimeInstanceDto
import java.time.LocalDateTime

data class WeatherInstance(
    val timestamp: LocalDateTime,
    val temperature: Double,
    val humidity: Double,
    val windSpeed: Double,
    val symbolCode: String?,
    val symbolUrl: String?,
    val precipitationAmount: Double?,
    val uvIndexClearSky: Double?,
) {
    companion object {
        fun toWeatherInstance(timeInstanceDto: TimeInstanceDto, timestamp: LocalDateTime): WeatherInstance {
            val details = timeInstanceDto.data.instant.details
            val nextOneHours = timeInstanceDto.data.nextOneHours
            return WeatherInstance(
                timestamp = timestamp,
                temperature = details.airTemperature,
                humidity = details.relativeHumidity,
                windSpeed = details.windSpeed,
                symbolCode = nextOneHours?.summary?.symbolCode,
                symbolUrl = if (nextOneHours != null) "https://api.met.no/images/weathericons/svg/${nextOneHours.summary.symbolCode}.svg" else null,
                precipitationAmount = nextOneHours?.details?.precipitationAmount,
                uvIndexClearSky = details.uvIndexClearSky,
            )
        }
    }
}