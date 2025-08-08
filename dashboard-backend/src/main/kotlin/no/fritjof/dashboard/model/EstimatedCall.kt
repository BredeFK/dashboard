package no.fritjof.dashboard.model

import no.fritjof.dashboard.dto.EstimatedCallDto
import java.time.LocalDateTime
import java.time.ZoneId

data class EstimatedCall(
    val realtime: Boolean,
    val aimedArrivalTime: LocalDateTime,
    val expectedArrivalTime: LocalDateTime,
    val frontText: String,
    val lineNumber: String,
    val transportMode: String,
    val boardingLocation: String,
    val presentation: Presentation
) {
    companion object {
        fun toEstimatedCall(estimatedCallDto: EstimatedCallDto): EstimatedCall {
            val line = estimatedCallDto.serviceJourney.journeyPattern.line

            val presentation: Presentation =
                if (line.presentation.colour != null && line.presentation.textColour != null) {
                    Presentation(
                        colour = "#${line.presentation.colour}",
                        textColour = "#${line.presentation.textColour}"
                    )
                } else {
                    Presentation(
                        colour = "#757575",
                        textColour = "#FFFFFF"
                    )
                }

            return EstimatedCall(
                realtime = estimatedCallDto.realtime,
                aimedArrivalTime = estimatedCallDto.aimedArrivalTime
                    .atZoneSameInstant(ZoneId.of("Europe/Oslo"))
                    .toLocalDateTime(),
                expectedArrivalTime = estimatedCallDto.expectedArrivalTime
                    .atZoneSameInstant(ZoneId.of("Europe/Oslo"))
                    .toLocalDateTime(),
                frontText = estimatedCallDto.destinationDisplay.frontText,
                lineNumber = line.publicCode,
                transportMode = line.transportMode,
                boardingLocation = estimatedCallDto.quay.description,
                presentation = presentation
            )
        }
    }
}