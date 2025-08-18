package no.fritjof.dashboard.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.fritjof.dashboard.model.Athlete
import no.fritjof.dashboard.service.strava.StravaService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.WeekFields

@Service
class DiscordService(
    @Qualifier("discordWebClient") private val webClient: WebClient,
    private val stravaService: StravaService
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun postScoreBoard() {
        val scoreboard = stravaService.getLastWeeksScoreBoard()
        val message = formatMessage(scoreboard)
        logger.info("Posting scoreboard to Discord")
        webClient.post()
            .bodyValue(message)
            .retrieve()
            .toBodilessEntity()
            .doOnError { logger.error("Error during posting scoreboard to Discord : $${it.message}", it.cause) }
            .doOnSuccess { logger.info("Successfully Posted scoreboard to Discord at ${LocalDateTime.now()}") }
            .block()
    }

    private fun formatMessage(athletes: List<Athlete>): String {
        val objectMapper = jacksonObjectMapper()

        val embed = objectMapper.createObjectNode()
        embed.put(
            "title",
            "Last weeks leaderboard for The Ginger Pigeons running club :man_running:"
        )
        embed.put("color", getColorOfTheWeek())

        val fieldAthlete = objectMapper.createObjectNode()
        val fieldAvgPace = objectMapper.createObjectNode()
        val fieldLongest = objectMapper.createObjectNode()

        fieldAthlete.put("name", "Athlete")
        fieldAthlete.put("inline", true)
        fieldAvgPace.put("name", "Avg. pace")
        fieldAvgPace.put("inline", true)
        fieldLongest.put("name", "Longest")
        fieldLongest.put("inline", true)

        val athleteValue = StringBuilder()
        val paceValue = StringBuilder()
        val longestValue = StringBuilder()

        athletes.forEachIndexed { index, athlete ->

            val runText = if (athlete.numberOfActivities == 1) "run" else "runs"

            athleteValue.append("**${index + 1}** ${athlete.fullName}: **${athlete.getTotalDistanceFormatted()}** (${athlete.numberOfActivities} $runText)\n\n")
            paceValue.append("${athlete.averagePacePrKm}\n\n")
            longestValue.append("${athlete.getLongestActivityFormatted()}\n\n")
        }

        fieldAthlete.put("value", athleteValue.toString())
        fieldAvgPace.put("value", paceValue.toString())
        fieldLongest.put("value", longestValue.toString())

        embed.putArray("fields").apply {
            add(fieldAthlete)
            add(fieldAvgPace)
            add(fieldLongest)
        }

        embed.putObject("footer").put("text", "Join us @ https://www.strava.com/clubs/ginger-pigeons")

        val payload = objectMapper.createObjectNode()
        payload.put("username", "The Ginger Pigeons StravaBot")
        payload.putArray("embeds").add(embed)

        return objectMapper.writeValueAsString(payload)
    }

    private fun getColorOfTheWeek(): Int {
        // https://coolors.co/palette/f94144-f3722c-f8961e-f9844a-f9c74f-90be6d-43aa8b-4d908e-577590-277da1
        val coolors = listOf(
            16335172, 15954476, 16291358, 16352330, 16369487,
            9485933, 4434571, 5083278, 5731728, 2588065
        )
        val week = LocalDate.now()[WeekFields.ISO.weekOfWeekBasedYear()]
        return coolors[(week - 1) % coolors.size]
    }
}
