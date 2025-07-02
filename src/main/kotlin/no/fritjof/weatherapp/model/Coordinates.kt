package no.fritjof.weatherapp.model

import no.fritjof.weatherapp.dto.NominatimDto

data class Coordinates(

    val latitude: Double,
    val longitude: Double,
    val addressType: String,
    val name: String,
    val displayName: String

) {
    companion object {
        fun toCoordinates(nominatimDto: NominatimDto): Coordinates {
            return Coordinates(
                latitude = nominatimDto.latitude,
                longitude = nominatimDto.longitude,
                addressType = nominatimDto.addressType,
                name = nominatimDto.name,
                displayName = nominatimDto.displayName
            )
        }
    }
}