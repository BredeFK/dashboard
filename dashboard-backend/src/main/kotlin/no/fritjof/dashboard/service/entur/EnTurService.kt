package no.fritjof.dashboard.service.entur

import no.fritjof.dashboard.dto.EnTurDto
import no.fritjof.dashboard.model.DepartureBoard
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class EnTurService(
    @Qualifier("enTurWebClient") private val webclient: WebClient,
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Cacheable("departureBoard", key = "#quayId + #timeRange + #numberOfDepartures")
    fun getDepartureBoard(
        quayId: String,
        timeRange: Long,
        numberOfDepartures: Int
    ): DepartureBoard? {
        val query = getDepartureBoardBody(quayId, timeRange, numberOfDepartures)
        val response = webclient.post()
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
}
