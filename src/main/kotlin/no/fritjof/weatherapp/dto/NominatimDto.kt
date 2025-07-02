package no.fritjof.weatherapp.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NominatimDto(

    @JsonProperty("lat")
    val latitude: Double,

    @JsonProperty("lon")
    val longitude: Double,

    @JsonProperty("addresstype")
    val addressType: String,

    val name: String,

    @JsonProperty("display_name")
    val displayName: String

)