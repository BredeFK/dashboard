package no.fritjof.dashboard.service

import no.fritjof.dashboard.dto.NominatimDto
import no.fritjof.dashboard.model.Coordinates
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class NominatimService(
    @Qualifier("nominatimWebClient") private val webClient: WebClient
) {
    private val format = "jsonv2"
    private val zoom = 12 // Town / Borough

    fun searchPlace(place: String): List<Coordinates>? {
        val response = webClient.get()
            .uri {
                it.path("search")
                    .queryParam("format", format)
                    .queryParam("q", place)
                    .build()
            }
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<NominatimDto>>() {})
            .block()

        return response?.map {
            Coordinates.toCoordinates(it)
        }
    }

    @Cacheable("locations", key = "#latitude.toString() + #longitude.toString()")
    fun searchCoordinates(latitude: Double, longitude: Double): NominatimDto? {
        return webClient.get()
            .uri {
                it.path("reverse")
                    .queryParam("format", format)
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .queryParam("zoom", zoom)
                    .build()
            }
            .retrieve()
            .bodyToMono(NominatimDto::class.java)
            .block()
    }

}
