package no.fritjof.dashboard.model

import java.time.LocalDateTime

data class Leaderboard(
    val athletes: List<Athlete>,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)
