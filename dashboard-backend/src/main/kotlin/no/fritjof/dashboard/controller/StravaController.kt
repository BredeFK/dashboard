package no.fritjof.dashboard.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import no.fritjof.dashboard.model.Athlete
import no.fritjof.dashboard.service.strava.StravaService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Strava Controller")
@Controller
@RequestMapping("/api/strava")
class StravaController(
    private val stravaService: StravaService
) {

    @GetMapping("leaderboard/now", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Get current club leaderboard for this week",
        description = "Get athletes from club in a sorted list by longest total distance",
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of athletes",
        content = [
            Content(
                mediaType = "application/json",
                array = ArraySchema(
                    schema = Schema(
                        implementation = Athlete::class
                    )
                )
            )
        ]
    )
    fun leaderboardForThisWeek(mock: Boolean = false): ResponseEntity<List<Athlete>?> {
        return ResponseEntity.ok(stravaService.getLeaderBoardForThisWeek(mock))
    }

    @GetMapping("leaderboard/last-week", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Get club leaderboard for last week",
        description = "Get athletes from club in a sorted list by longest total distance",
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of athletes",
        content = [
            Content(
                mediaType = "application/json",
                array = ArraySchema(
                    schema = Schema(
                        implementation = Athlete::class
                    )
                )
            )
        ]
    )
    fun getLeaderboardForLastWeek(): ResponseEntity<List<Athlete>> {
        return ResponseEntity.ok(stravaService.getLeaderboardForLastWeek())
    }

}
