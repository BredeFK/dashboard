package no.fritjof.dashboard.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class EnTurGeocoderDto(
    val features: List<Feature>
)

data class Feature(
    val properties: Properties
)

data class Properties(
    val id: String,
    val gid: String,
    val layer: String,
    val source: String,

    @JsonProperty("source_id")
    val sourceId: String,
    val name: String,
    val street: String?,
    val confidence: Float,
    val distance: Float,
    val accuracy: String,

    @JsonProperty("country_a")
    val country: String,

    val county: String,

    @JsonProperty("county_gid")
    val countyGid: String,

    val locality: String,

    @JsonProperty("locality_gid")
    val localityGid: String,

    val borough: String?,

    @JsonProperty("borough_gid")
    val boroughGid: String?,
    val label: String,
    val category: List<String>
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
    val name: String,
)
