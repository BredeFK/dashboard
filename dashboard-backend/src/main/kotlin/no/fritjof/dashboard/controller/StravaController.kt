package no.fritjof.dashboard.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import no.fritjof.dashboard.model.Leaderboard
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
        description = "Get leaderboard containing athletes from club in a sorted list by longest total distance",
    )
    @ApiResponse(
        responseCode = "200",
        description = "Leaderboard with list of athletes and time range",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Leaderboard::class)
            )
        ]
    )
    fun leaderboardForThisWeek(mock: Boolean = false): ResponseEntity<Leaderboard?> {
        return ResponseEntity.ok(stravaService.getLeaderBoardForThisWeek(mock))
    }

    @GetMapping("leaderboard/last-week", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Get club leaderboard for last week",
        description = "Get leaderboard containing athletes from club in a sorted list by longest total distance",
    )
    @ApiResponse(
        responseCode = "200",
        description = "Leaderboard with list of athletes and time range",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = Leaderboard::class)
            )
        ]
    )
    fun getLeaderboardForLastWeek(): ResponseEntity<Leaderboard> {
        return ResponseEntity.ok(stravaService.getLeaderboardForLastWeek())
    }

}
