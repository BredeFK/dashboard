package no.fritjof.dashboard.service.strava

import com.fasterxml.jackson.annotation.JsonProperty

data class StravaToken(

    @JsonProperty("expires_at")
    val expiresAt: Long,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    @JsonProperty("access_token")
    val accessToken: String

) {
    fun hasExpired(): Boolean {
        return this.expiresAt > System.currentTimeMillis()
    }
}