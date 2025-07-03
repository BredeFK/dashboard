package no.fritjof.weatherapp.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class StravaActivityDto(
    private val athlete: AthleteDto,
    val distance: Double,

    @JsonProperty("moving_time")
    val movingTime: Double,

    @JsonProperty("total_elevation_gain")
    val totalElevationGain: Double
) {
    fun fullName() = "${this.athlete.firstname} ${this.athlete.lastname}"
}

data class AthleteDto(
    val firstname: String,
    val lastname: String
)