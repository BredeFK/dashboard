package no.fritjof.weatherapp.service.strava

import org.springframework.stereotype.Component

@Component
class StravaTokenStore {

    @Volatile
    private var token: StravaToken? = null

    fun save(token: StravaToken) {
        this.token = token
    }

    fun get(): StravaToken? = this.token
}