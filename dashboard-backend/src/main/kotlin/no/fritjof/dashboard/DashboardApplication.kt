package no.fritjof.dashboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class DashboardApplication

fun main(args: Array<String>) {
    runApplication<DashboardApplication>(*args)
}
