package no.fritjof.weatherapp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class NominatimService(
    @param:Value($$"${nominatim.base-url}") private val nominatimUrl: String,
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    fun searchPlace(place: String): List<Coordinates>? {
        val response = getWebClient().get()
            .uri {
                it.path("search")
                    .queryParam("format", "json")
                    .queryParam("q", place)
                    .build()
            }
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<Coordinates>>() {})
            .block()

        return response
    }

    // TODO : Find a better way for this
    private fun getWebClient(): WebClient {
        return WebClient.builder()
            .filter(requestLoggerFilter())
            .filter(responseLoggerFilter())
            .baseUrl(nominatimUrl)
            .build()
    }

    private fun requestLoggerFilter() = ExchangeFilterFunction.ofRequestProcessor {
        log.info("${it.method()} ${it.url()}")
        Mono.just(it)
    }

    private fun responseLoggerFilter() = ExchangeFilterFunction.ofResponseProcessor {
        log.info("${it.request().method} ${it.request().uri} [${it.statusCode().value()}]")
        Mono.just(it)
    }
}