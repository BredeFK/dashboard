package no.fritjof.dashboard.config

import no.fritjof.dashboard.service.DiscordService
import no.fritjof.dashboard.service.strava.StravaService
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class DiscordConfig(
    private val discordService: DiscordService,
) {

    @Scheduled(cron = "0 0 10 * * 1", zone = "ECT")
    fun postScoreboard() {
        discordService.postScoreBoard()
    }
}