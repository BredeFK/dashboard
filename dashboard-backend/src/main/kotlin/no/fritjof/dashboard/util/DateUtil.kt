package no.fritjof.dashboard.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.TimeZone

object DateUtil {

    fun toLocalDateTime(datetime: String?): LocalDateTime {
        if (datetime == null) {
            throw NullPointerException("datetime is null")
        }
        return OffsetDateTime.parse(datetime)
            .atZoneSameInstant(TimeZone.getTimeZone("ECT").toZoneId())
            .toLocalDateTime()
    }

}
