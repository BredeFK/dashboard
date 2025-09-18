package no.fritjof.dashboard.model

import no.fritjof.dashboard.dto.TimeInstanceDto
import no.fritjof.dashboard.util.DateUtil.toLocalDateTime
import java.time.LocalDateTime

data class ImmediateWeatherInstance(
    val timestamp: LocalDateTime,
    val precipitationRate: Double
) {
    companion object {
        fun toWeatherInstance(timeInstanceDto: TimeInstanceDto): ImmediateWeatherInstance {
            return ImmediateWeatherInstance(
                timestamp = toLocalDateTime(timeInstanceDto.time),
                precipitationRate = timeInstanceDto.data.instant.details.precipitationRate ?: 0.0
            )
        }
    }
}
