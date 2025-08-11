package no.fritjof.dashboard.model

import no.fritjof.dashboard.dto.EnTurDto

data class DepartureBoard(
    val id: String,
    val name: String,
    val estimatedCalls: List<EstimatedCall>,
) {
    companion object {
        fun toDepartureBoard(enTurDto: EnTurDto, quayId: String): DepartureBoard {
            val estimatedCalls =
                enTurDto.data.stopPlace.estimatedCalls.filter {
                    it.serviceJourney.journeyPattern.line.authority.name == "Ruter" &&
                            it.quay.id == quayId
                }.map {
                    EstimatedCall.toEstimatedCall(it)
                }
            return DepartureBoard(
                id = enTurDto.data.stopPlace.id,
                name = enTurDto.data.stopPlace.name,
                estimatedCalls = estimatedCalls
            )
        }
    }
}
