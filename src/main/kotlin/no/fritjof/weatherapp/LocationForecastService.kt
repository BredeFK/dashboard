package no.fritjof.weatherapp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class LocationForecastService(
    @param:Value($$"${location-forecast.base-url}") private val locationForecastBaseUrl: String,
    @param:Value($$"${location-forecast.user-agent}") private val userAgent: String
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    fun getCompact(latitude: Double, longitude: Double): String? {
        val webClient = getWebClient()
        return webClient.get()
            .uri {
                it.path("/compact")
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    private fun getWebClient(): WebClient {
        return WebClient.builder()
            .filter(requestLoggerFilter())
            .filter(responseLoggerFilter())
            .baseUrl(locationForecastBaseUrl)
            .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
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