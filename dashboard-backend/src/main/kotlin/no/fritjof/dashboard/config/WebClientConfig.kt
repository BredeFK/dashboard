package no.fritjof.dashboard.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class WebClientConfig(
    @Value($$"${location-forecast.base-url}") private val locationForecastBaseUrl: String,
    @Value($$"${location-forecast.user-agent}") private val userAgent: String,
    @Value($$"${nominatim.base-url}") private val nominatimUrl: String,
    @Value($$"${strava.base-url}") private val stravaUrl: String,
    @Value($$"${discord.base-url}") private val discordUrl: String,
    @Value($$"${discord.webhook-url-path}") private val discordWebhookPath: String,
    @Value($$"${en-tur.base-url}") private val enTurBaseUrl: String,
    @Value($$"${en-tur.et-client-name}") private val enTurClientName: String,
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val enTurHeaderName = "ET-Client-Name"

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

    @Bean
    fun discordWebClient(): WebClient {
        return WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(requestLoggerFilter())
            .filter(responseLoggerFilter())
            .baseUrl("$discordUrl/$discordWebhookPath")
            .build()
    }

    @Bean
    fun enTurWebClient(): WebClient {
        return WebClient.builder()
            .filter(requestLoggerFilter())
            .filter(responseLoggerFilter())
            .baseUrl(enTurBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(enTurHeaderName, enTurClientName)
            .build()
    }

    private fun requestLoggerFilter() = ExchangeFilterFunction.ofRequestProcessor {
        if (stravaUrl.contains(it.url().host) && it.url().path.contains("token")) {
            log.info("${it.method()} ${it.url().toString().split("?")[0]}")
        } else if (discordUrl.contains(it.url().host)) {
            log.info("${it.method()} $discordUrl")
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
        } else if (discordUrl.contains(uri.host)) {
            log.info("${it.request().method} $discordUrl [${it.statusCode().value()}]")
        } else {
            log.info("${it.request().method} ${it.request().uri} [${it.statusCode().value()}]")
        }
        Mono.just(it)
    }
}