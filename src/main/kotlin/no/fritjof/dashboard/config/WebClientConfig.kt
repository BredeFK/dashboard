package no.fritjof.dashboard.config

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
    @Value($$"${location-forecast.base-url}") private val locationForecastBaseUrl: String,
    @Value($$"${location-forecast.user-agent}") private val userAgent: String,
    @Value($$"${nominatim.base-url}") private val nominatimUrl: String,
    @Value($$"${strava.base-url}") private val stravaUrl: String
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

    @Bean
    fun stravaWebClient(): WebClient {
        return WebClient.builder()
            .filter(requestLoggerFilter())
            .filter(responseLoggerFilter())
            .baseUrl(stravaUrl)
            .build()
    }

    private fun requestLoggerFilter() = ExchangeFilterFunction.ofRequestProcessor {
        if (stravaUrl.contains(it.url().host) && it.url().path.contains("token")) {
            log.info("${it.method()} ${it.url().toString().split("?")[0]}")
        } else {
            log.info("${it.method()} ${it.url()}")
        }
        Mono.just(it)
    }

    private fun responseLoggerFilter() = ExchangeFilterFunction.ofResponseProcessor {
        val uri = it.request().uri
        if (stravaUrl.contains(uri.host) && uri.path.contains("token")) {
            log.info(
                "${it.request().method} " +
                        "${it.request().uri.toString().split("?")[0]} [${it.statusCode().value()}]"
            )
        } else {
            log.info("${it.request().method} ${it.request().uri} [${it.statusCode().value()}]")
        }
        Mono.just(it)
    }
}