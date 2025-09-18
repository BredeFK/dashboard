package no.fritjof.dashboard.model

import no.fritjof.dashboard.dto.TimeInstanceDto
import java.time.LocalDateTime

data class WeatherInstance(
    val timestamp: LocalDateTime,
    val temperature: Double,
    val humidity: Double,
    val windSpeed: Double,
    val symbolCode: String?,
    val symbolUrl: String?,
    val precipitationAmountMin: Double?,
    val precipitationAmountMax: Double?,
    val uvIndexClearSky: Double?,
) {
    companion object {
        fun toWeatherInstance(timeInstanceDto: TimeInstanceDto, timestamp: LocalDateTime): WeatherInstance {
            val details = timeInstanceDto.data.instant.details
            val nextOneHours = timeInstanceDto.data.nextOneHours
            return WeatherInstance(
                timestamp = timestamp,
                temperature = details.airTemperature ?: 0.0,
                humidity = details.relativeHumidity ?: 0.0,
                windSpeed = details.windSpeed ?: 0.0,
                symbolCode = nextOneHours?.summary?.symbolCode,
                symbolUrl = if (nextOneHours != null) "https://api.met.no/images/weathericons/svg/${nextOneHours.summary.symbolCode}.svg" else null,
                precipitationAmountMin = nextOneHours?.details?.precipitationAmountMin,
                precipitationAmountMax = nextOneHours?.details?.precipitationAmountMax,
                uvIndexClearSky = details.uvIndexClearSky,
            )
        }
    }
}
