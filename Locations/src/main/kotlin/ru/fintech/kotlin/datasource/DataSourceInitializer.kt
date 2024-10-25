package ru.fintech.kotlin.datasource

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class DataSourceInitializer {
    protected val log: Logger = LoggerFactory.getLogger(this.javaClass)

    abstract fun initializeData()
}