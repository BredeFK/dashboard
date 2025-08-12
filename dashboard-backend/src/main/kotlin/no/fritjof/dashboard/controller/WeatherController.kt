package no.fritjof.dashboard.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import no.fritjof.dashboard.model.WeatherForecast
import no.fritjof.dashboard.service.LocationForecastService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Weather Controller")
@Controller
@RequestMapping("/api/weather")
class WeatherController(
    private val locationForecastService: LocationForecastService
) {

    @GetMapping("forecast", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Get weather forcast",
        description = "Weather forecast for a specified place",
    )
    @ApiResponse(responseCode = "200", description = "Compact information")
    fun compact(latitude: Double, longitude: Double): ResponseEntity<WeatherForecast> {
        val response = locationForecastService.getWeatherForecast(latitude, longitude)
        return ResponseEntity.ok(response)
    }
}
