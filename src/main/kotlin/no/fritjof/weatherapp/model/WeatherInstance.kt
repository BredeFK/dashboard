package no.fritjof.weatherapp.model

import no.fritjof.weatherapp.dto.TimeInstanceDto
import java.time.LocalDateTime

data class WeatherInstance(
    val timestamp: LocalDateTime,
    val temperature: Double,
    val humidity: Double,
    val windSpeed: Double,
    val symbolCode: String?,
    val precipitationAmount: Double?
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
                precipitationAmount = nextOneHours?.details?.precipitationAmount
            )
        }
    }
}