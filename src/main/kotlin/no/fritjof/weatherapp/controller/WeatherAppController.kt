package no.fritjof.weatherapp.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Weather Controller")
@Controller
@RequestMapping("/api")
class WeatherAppController {

    @GetMapping("/ping", produces = ["text/plain"])
    @Operation(
        summary = "Ping the server",
        description = "Ping the server to check if it is running",
    )
    @ApiResponse(responseCode = "200", description = "pong")
    fun ping(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }
}