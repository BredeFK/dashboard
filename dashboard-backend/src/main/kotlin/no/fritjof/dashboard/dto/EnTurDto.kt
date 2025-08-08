package no.fritjof.dashboard.dto

import java.time.LocalDate
import java.time.OffsetDateTime

data class EnTurDto(
    val data: EnTurDataDto
)

data class EnTurDataDto(
    val stopPlace: StopPlaceDto
)

data class StopPlaceDto(
    val id: String,
    val name: String,
    val estimatedCalls: List<EstimatedCallDto>
)

data class EstimatedCallDto(
    val realtime: Boolean,
    val aimedArrivalTime: OffsetDateTime,
    val aimedDepartureTime: OffsetDateTime,
    val expectedArrivalTime: OffsetDateTime,
    val expectedDepartureTime: OffsetDateTime,
    val actualArrivalTime: OffsetDateTime?,
    val actualDepartureTime: OffsetDateTime?,
    val date: LocalDate,
    val forBoarding: Boolean,
    val forAlighting: Boolean,
    val destinationDisplay: DestinationDisplayDto,
    val quay: QuayDto,
    val serviceJourney: ServiceJourneyDto
)

data class DestinationDisplayDto(
    val frontText: String
)

data class QuayDto(
    val id: String,
    val name: String,
    val description: String,
    val publicCode: String?
)

data class ServiceJourneyDto(
    val journeyPattern: JourneyPatternDto
)

data class JourneyPatternDto(
    val line: LineDto
)

data class LineDto(
    val id: String,
    val name: String,
    val transportMode: String,
    val publicCode: String,
    val operator: OperatorDto,
    val authority: AuthorityDto,
    val presentation: PresentationDto
)

data class OperatorDto(
    val id: String,
    val name: String
)

data class AuthorityDto(
    val name: String
)

data class PresentationDto(
    val colour: String?,
    val textColour: String?
)