package no.fritjof.dashboard.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import no.fritjof.dashboard.model.DepartureBoard
import no.fritjof.dashboard.service.entur.EnTurService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Public Transport Controller")
@Controller
@RequestMapping("/api/public-transport")
class PublicTransportController(
    private val enTurService: EnTurService
) {

    @GetMapping("departure-board", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Get departure board",
        description = "Returns a list of departure times for a specified stopPlace",
    )
    @ApiResponse(responseCode = "200", description = "OK")
    fun getDepartureBoard(
        stopPlaceId: String,
        quayId: String,
        timeRange: Long = 72100,
        numberOfDepartures: Int = 20 // Not accurate number after filtering
    ): ResponseEntity<DepartureBoard> {
        val response = enTurService.getDepartureBoard(stopPlaceId, quayId, timeRange, numberOfDepartures)
        return ResponseEntity.ok(response)
    }
}
