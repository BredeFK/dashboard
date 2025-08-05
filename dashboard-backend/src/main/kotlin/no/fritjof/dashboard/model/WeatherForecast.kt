package no.fritjof.dashboard.model

import java.time.LocalDateTime

data class WeatherForecast(
    val lastUpdated: LocalDateTime,
    val weatherSeries: List<WeatherInstance>
)