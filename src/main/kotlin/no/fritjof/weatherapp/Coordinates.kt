package no.fritjof.weatherapp

import com.fasterxml.jackson.annotation.JsonProperty

data class Coordinates(

    @param:JsonProperty("lat")
    val latitude: Double,

    @param:JsonProperty("lon")
    val longitude: Double,

    @param:JsonProperty("addresstype")
    val addressType: String,

    val name: String,

    @param:JsonProperty("display_name")
    val displayName: String

)