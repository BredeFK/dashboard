package no.fritjof.dashboard.model

import no.fritjof.dashboard.dto.LocationForecastDto
import no.fritjof.dashboard.util.DateUtil.toLocalDateTime
import java.time.LocalDateTime

data class ImmediateWeatherForecast(
    val lastUpdated: LocalDateTime,
    val radarCoverage: String?,
    val currentTimeInstant: CurrentWeatherInstance,
    val weatherSeries: List<ImmediateWeatherInstance>
) {
    companion object {
        fun toDto(locationForecastDto: LocationForecastDto) = ImmediateWeatherForecast(
            lastUpdated = toLocalDateTime(locationForecastDto.properties.meta.updatedAt),
            radarCoverage = locationForecastDto.properties.meta.radarCoverage,
            currentTimeInstant = CurrentWeatherInstance.toDto(locationForecastDto.properties.timeSeries[0]),
            weatherSeries = locationForecastDto.properties.timeSeries
                .subList(1, locationForecastDto.properties.timeSeries.size)
                .map {
                    ImmediateWeatherInstance.toWeatherInstance(it)
                }
        )
    }
}
