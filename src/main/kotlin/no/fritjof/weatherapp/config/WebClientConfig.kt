package no.fritjof.weatherapp.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class WebClientConfig(
    @param:Value($$"${location-forecast.base-url}") private val locationForecastBaseUrl: String,
    @param:Value($$"${location-forecast.user-agent}") private val userAgent: String,
    @param:Value($$"${nominatim.base-url}") private val nominatimUrl: String,
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun locationForecastWebClient(): WebClient {
        return WebClient.builder()
            .filter(requestLoggerFilter())
            .filter(responseLoggerFilter())
            .baseUrl(locationForecastBaseUrl)
            .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
            .build()
    }

    @Bean
    fun nominatimWebClient(): WebClient {
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
        log.info("${it.request().method} ${it.request().uri} [${it.statusCode().value()}]" )
        Mono.just(it)
    }
}