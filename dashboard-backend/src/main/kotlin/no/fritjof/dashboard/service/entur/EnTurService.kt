package no.fritjof.dashboard.service.entur

import no.fritjof.dashboard.dto.EnTurDto
import no.fritjof.dashboard.dto.EnTurGeocoderDto
import no.fritjof.dashboard.model.DepartureBoard
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class EnTurService(
    @Qualifier("enTurWebClient") private val webclient: WebClient,
    @Value($$"${en-tur.geocoder-path}") private val geocoderPath: String,
    @Value($$"${en-tur.journey-planner-path}") private val journeyPlannerPath: String,
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val boundaryCircleRadius = 1
    private val size = 10
    private val layers = "address,locality"
    private val categories = listOf("tettsted", "attraction", "tettbebyggelse", "street")

    @Cacheable("departureBoard", key = "#quayId + #timeRange + #numberOfDepartures")
    fun getDepartureBoard(
        quayId: String,
        timeRange: Long,
        numberOfDepartures: Int
    ): DepartureBoard? {
        val query = getDepartureBoardBody(quayId, timeRange, numberOfDepartures)
        val response = webclient.post()
            .uri(journeyPlannerPath)
            .bodyValue(query)
            .retrieve()
            .bodyToMono(EnTurDto::class.java)
            .block()
        if (response == null) return null
        return DepartureBoard.toDepartureBoard(response)
    }

    private fun getDepartureBoardBody(quayId: String, timeRange: Long, numberOfDepartures: Int): Query {
        return Query(
            query =
                """
                query {
                  quay(id: "$quayId") {
                    id
                    name
                    description
                    publicCode
                    estimatedCalls(timeRange: $timeRange, numberOfDepartures: $numberOfDepartures) {
                      realtime
                      aimedArrivalTime
                      aimedDepartureTime
                      expectedArrivalTime
                      expectedDepartureTime
                      actualArrivalTime
                      actualDepartureTime
                      date
                      forBoarding
                      forAlighting
                      destinationDisplay {
                        frontText
                      }
                      serviceJourney {
                        journeyPattern {
                          line {
                            id
                            name
                            publicCode
                            transportMode
                            authority {
                              name
                            }
                            presentation {
                              colour
                              textColour
                            }
                            operator {
                              id
                              name
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """.trimIndent()
        )
    }

    @Cacheable("locations", key = "#latitude.toString() + #longitude.toString()")
    fun searchCoordinates(latitude: Double, longitude: Double): String {
        val response = webclient.get()
            .uri {
                it.path(geocoderPath)
                    .queryParam("point.lat", latitude)
                    .queryParam("point.lon", longitude)
                    .queryParam("boundary.circle.radius", boundaryCircleRadius)
                    .queryParam("size", size)
                    .queryParam("layers", layers)
                    .queryParam("categories", categories.joinToString(","))
                    .build()
            }
            .retrieve()
            .bodyToMono(EnTurGeocoderDto::class.java)
            .block()

        return getBestName(response)
    }

    private fun getBestName(enTurGeocoderDto: EnTurGeocoderDto?): String {
        if (enTurGeocoderDto == null || enTurGeocoderDto.features.isEmpty()) {
            return "Ukjent navn"
        }

        /* TODO : Uncomment for debugging or remove when done
        println()
        enTurGeocoderDto.features.forEach { log.info("${it.properties.label} -> ${it.properties.category}") }
        println()
         */

        for (preferredCategory in categories) {
            enTurGeocoderDto.features
                .firstOrNull { feature -> preferredCategory in feature.properties.category }
                ?.let {
                    return it.properties.label
                }
        }

        return enTurGeocoderDto.features.first().properties.label
    }

}
