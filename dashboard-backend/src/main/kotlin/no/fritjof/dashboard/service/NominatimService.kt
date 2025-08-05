package no.fritjof.dashboard.service

import no.fritjof.dashboard.dto.NominatimDto
import no.fritjof.dashboard.model.Coordinates
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class NominatimService(
    @Qualifier("nominatimWebClient") private val webClient: WebClient
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    fun searchPlace(place: String): List<Coordinates>? {
        val response = webClient.get()
            .uri {
                it.path("search")
                    .queryParam("format", "json")
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

}