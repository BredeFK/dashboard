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
            .recordStats()
            .buildAsync<Any, Any>()

        caffeineCacheManager.registerCustomCache("athletes", athletesCache)
        return caffeineCacheManager
    }
}
