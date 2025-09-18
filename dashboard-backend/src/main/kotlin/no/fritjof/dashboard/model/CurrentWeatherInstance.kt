package no.fritjof.dashboard.model

import no.fritjof.dashboard.dto.TimeInstanceDto

data class InstantDetails(
    val airTemperature: Double,
    val precipitationRate: Double,
    val relativeHumidity: Double,
    val windFromDirection: Double,
    val windSpeed: Double,
    val windSpeedOfGust: Double
)

data class NextOneHourDetails(
    val symbolCode: String,
    val precipitationAmount: Double,
)

data class CurrentWeatherInstance(
    val instantDetails: InstantDetails,
    val nextOneHourDetails: NextOneHourDetails
) {
    companion object {
        fun toDto(timeInstanceDto: TimeInstanceDto): CurrentWeatherInstance {
            val instantDetails = timeInstanceDto.data.instant.details
            val nextOneHour = timeInstanceDto.data.nextOneHours
            return CurrentWeatherInstance(
                instantDetails = InstantDetails(
                    airTemperature = instantDetails.airTemperature ?: 0.0,
                    precipitationRate = instantDetails.precipitationRate ?: 0.0,
                    relativeHumidity = instantDetails.relativeHumidity ?: 0.0,
                    windFromDirection = instantDetails.windFromDirection ?: 0.0,
                    windSpeed = instantDetails.windSpeed ?: 0.0,
                    windSpeedOfGust = instantDetails.windSpeedOfGust ?: 0.0
                ),
                nextOneHourDetails = NextOneHourDetails(
                    symbolCode = nextOneHour?.summary?.symbolCode ?: "",
                    precipitationAmount = nextOneHour?.details?.precipitationAmount ?: 0.0
                )
            )
        }
    }
}
