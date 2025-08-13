package no.fritjof.dashboard.model

import no.fritjof.dashboard.dto.EnTurDto

data class DepartureBoard(
    val id: String,
    val name: String,
    val description: String?,
    val estimatedCalls: List<EstimatedCall>,
) {
    companion object {
        fun toDepartureBoard(enTurDto: EnTurDto): DepartureBoard {
            val estimatedCalls =
                enTurDto.data.quay.estimatedCalls.filter {
                    it.serviceJourney.journeyPattern.line.authority.name == "Ruter"
                }.map {
                    EstimatedCall.toEstimatedCall(it)
                }
            return DepartureBoard(
                id = enTurDto.data.quay.id,
                name = enTurDto.data.quay.name,
                description = enTurDto.data.quay.description,
                estimatedCalls = estimatedCalls
            )
        }
    }
}
