package ru.fintech.kotlin.datasource

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener

abstract class DataSourceInitializer: ApplicationListener<ApplicationReadyEvent> {
    protected val log: Logger = LoggerFactory.getLogger(this.javaClass)

    abstract fun initializeData()
}