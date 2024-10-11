package ru.fintech.kotlin.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(ExecutorsProperties::class)
@Configuration
class ExecutorsConfiguration

@ConfigurationProperties(prefix = "initializer.executors")
class ExecutorsProperties (
    val fixedPoolSize: Int = 4,
    val scheduledPoolSize: Int = 2
)
