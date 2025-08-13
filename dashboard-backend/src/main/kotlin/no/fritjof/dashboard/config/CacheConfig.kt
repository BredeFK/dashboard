package no.fritjof.dashboard.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig {

    @Bean
    fun caffeineCacheManager(): CaffeineCacheManager {
        val caffeineCacheManager = CaffeineCacheManager()

        val athletesCache = Caffeine.newBuilder()
            .maximumSize(50)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .buildAsync<Any, Any>()

        val locationsCache = Caffeine.newBuilder()
            .maximumSize(100)
            .buildAsync<Any, Any>()

        val weatherCache = Caffeine.newBuilder()
            .maximumSize(50)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .buildAsync<Any, Any>()

        caffeineCacheManager.registerCustomCache("athletes", athletesCache)
        caffeineCacheManager.registerCustomCache("locations", locationsCache)
        caffeineCacheManager.registerCustomCache("weather", weatherCache)

        return caffeineCacheManager
    }
}
