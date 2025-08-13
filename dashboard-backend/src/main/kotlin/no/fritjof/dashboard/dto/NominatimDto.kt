package no.fritjof.dashboard.dto

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
    val displayName: String,

    val address: AddressDto?
)

data class AddressDto(
    @JsonProperty("city_district")
    val cityDistrict: String?,
    val city: String?,
    val municipality: String?,
    val county: String?,
    val country: String,

    @JsonProperty("country_code")
    val countryCode: String,
)
