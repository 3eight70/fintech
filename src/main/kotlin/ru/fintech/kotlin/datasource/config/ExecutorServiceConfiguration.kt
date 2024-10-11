package ru.fintech.kotlin.datasource.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.fintech.kotlin.config.ExecutorsProperties
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

@Configuration
class ExecutorServiceConfiguration(
    private val properties: ExecutorsProperties
) {
    @Bean("locationFixedThreadPool")
    fun locationFixedThreadPool(): ExecutorService {
        return Executors.newFixedThreadPool(properties.fixedPoolSize).apply {
            Thread.currentThread().name = "LocationFixedThreadPool"
        }
    }

    @Bean("categoryFixedThreadPool")
    fun categoryFixedThreadPool(): ExecutorService {
        return Executors.newFixedThreadPool(properties.fixedPoolSize).apply {
            Thread.currentThread().name = "CategoryFixedThreadPool"
        }
    }

    @Bean("scheduledThreadPool")
    fun scheduledThreadPool(): ScheduledExecutorService {
        return Executors.newScheduledThreadPool(properties.scheduledPoolSize).apply {
            Thread.currentThread().name = "ScheduledThreadPool"
        }
    }
}