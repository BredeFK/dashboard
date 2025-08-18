package no.fritjof.dashboard.config

import no.fritjof.dashboard.service.DiscordService
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class DiscordConfig(
    private val discordService: DiscordService,
) {

    // @Scheduled(cron = "0 0 10 * * 1", zone = "ECT") // TODO : Uncomment
    fun postLeaderboard() {
        discordService.postLeaderboard()
    }
}
