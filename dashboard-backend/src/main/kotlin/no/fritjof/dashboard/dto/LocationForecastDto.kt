package no.fritjof.dashboard.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LocationForecastDto(
    val properties: PropertiesDto
)

data class PropertiesDto(
    val meta: MetaDto,

    @JsonProperty("timeseries")
    val timeSeries: List<TimeInstanceDto>
)

data class MetaDto(
    @JsonProperty("updated_at")
    val updatedAt: String,

    @JsonProperty("radar_coverage")
    val radarCoverage: String?
)

data class TimeInstanceDto(
    val time: String,
    val data: DataDto
)

data class DataDto(
    val instant: InstantDto,

    @JsonProperty("next_1_hours")
    val nextOneHours: NextOneHoursDto?
)

data class NextOneHoursDto(
    val summary: SummaryDto,
    val details: NextHoursDetailsDto
)

data class SummaryDto(
    @JsonProperty("symbol_code")
    val symbolCode: String
)

data class NextHoursDetailsDto(
    @JsonProperty("precipitation_amount_min")
    val precipitationAmountMin: Double?,

    @JsonProperty("precipitation_amount_max")
    val precipitationAmountMax: Double?,

    @JsonProperty("precipitation_amount")
    val precipitationAmount: Double?
)

data class InstantDto(
    val details: DetailsDto
)

data class DetailsDto(

    @JsonProperty("air_pressure_at_sea_level")
    val airPressureAtSeaLevel: Double?,

    @JsonProperty("air_temperature")
    val airTemperature: Double?,

    @JsonProperty("cloud_area_fraction")
    val cloudAreaFraction: Double?,

    @JsonProperty("relative_humidity")
    val relativeHumidity: Double?,

    @JsonProperty("wind_from_direction")
    val windFromDirection: Double?,

    @JsonProperty("wind_speed")
    val windSpeed: Double?,

    @JsonProperty("wind_speed_of_gust")
    val windSpeedOfGust: Double?,

    @JsonProperty("ultraviolet_index_clear_sky")
    val uvIndexClearSky: Double?,

    @JsonProperty("precipitation_rate")
    val precipitationRate: Double?,

    @JsonProperty("precipitation_amount")
    val precipitationAmount: Double?

)
