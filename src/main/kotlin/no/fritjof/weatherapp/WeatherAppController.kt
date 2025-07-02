package no.fritjof.weatherapp

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import no.fritjof.weatherapp.LocationForecastService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Weather Controller")
@Controller
@RequestMapping("/api/weather")
class WeatherAppController(
    private val locationForecastService: LocationForecastService
) {

    @GetMapping("/ping", produces = [MediaType.TEXT_PLAIN_VALUE])
    @Operation(
        summary = "Ping the server",
        description = "Ping the server to check if it is running",
    )
    @ApiResponse(responseCode = "200", description = "pong")
    fun ping(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }

    @GetMapping("compact", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Compact information",
        description = "Weather forecast for a specified place",
    )
    @ApiResponse(responseCode = "200", description = "Compact information")
    fun compact(latitude: Double, longitude: Double): ResponseEntity<String?> {
        val response = locationForecastService.getCompact(latitude, longitude)
        if (response != null) {
            return ResponseEntity.ok(response)
        }
        return ResponseEntity.badRequest().body(null)
    }
}