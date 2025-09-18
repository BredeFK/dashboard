package no.fritjof.dashboard.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import no.fritjof.dashboard.model.ImmediateWeatherForecast
import no.fritjof.dashboard.model.WeatherForecast
import no.fritjof.dashboard.service.WeatherApiService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Weather Controller")
@Controller
@RequestMapping("/api/weather")
class WeatherController(
    private val weatherApiService: WeatherApiService
) {

    @GetMapping("forecast", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Get weather forcast",
        description = "Weather forecast for a specified place",
    )
    @ApiResponse(responseCode = "200", description = "Compact information")
    fun weatherForecast(latitude: Double, longitude: Double): ResponseEntity<WeatherForecast> {
        val response = weatherApiService.getWeatherForecast(latitude, longitude)
        return ResponseEntity.ok(response)
    }

    @GetMapping("immediate", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Get immediate weather forcast",
        description = "Immediate weather forecast for specified locations in the Nordic area",
    )
    @ApiResponse(responseCode = "200", description = "Compact information")
    fun immediateWeatherForcast(
        latitude: Double = 59.910276, // TODO : Remove
        longitude: Double = 10.76577 // TODO : Remove
    ): ResponseEntity<ImmediateWeatherForecast> {
        val response = weatherApiService.getImmediateWeatherForecast(latitude, longitude)
        return ResponseEntity.ok(response)
    }
}
