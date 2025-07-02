package no.fritjof.weatherapp.model

import java.time.LocalDateTime

data class WeatherForecast(
    val lastUpdated: LocalDateTime,
    val weatherSeries: List<WeatherInstance>
)