package no.fritjof.weatherapp.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import no.fritjof.weatherapp.model.Coordinates
import no.fritjof.weatherapp.service.NominatimService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Coordinates Controller")
@Controller
@RequestMapping("/api/coordinates")
class CoordinatesController(
    private val nominatimService: NominatimService
) {

    @GetMapping("search", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Search location ",
        description = "Search for a location and get a list of coordinates",
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of coordinates",
        content = [
            Content(
                mediaType = "application/json",
                array = ArraySchema(
                    schema = Schema(
                        implementation = Coordinates::class
                    )
                )
            )
        ]
    )
    fun search(@RequestParam place: String): ResponseEntity<List<Coordinates>?> {
        val response = nominatimService.searchPlace(place)
        if (!response.isNullOrEmpty()) {
            return ResponseEntity.ok(response)
        }
        return ResponseEntity.badRequest().body(null)
    }
}