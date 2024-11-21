package ru.fintech.kotlin

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.fintech.kotlin.datasource.DataSourceInitializer
import ru.fintech.kotlin.datasource.EntityScanner

@SpringBootApplication(scanBasePackages = ["ru.fintech.kotlin"])
class HomeworkApplication(private val dataSourceInitializer: List<DataSourceInitializer>) : CommandLineRunner {
    private val log = LoggerFactory.getLogger(HomeworkApplication::class.java)

    override fun run(vararg args: String?) {
        log.info("Процесс кастомной инициализации начат")
        EntityScanner.initialize("ru.fintech.kotlin")

        for (initializer in dataSourceInitializer) {
            initializer.initializeData()
        }

        log.info("Инициализация завершена")
    }
}

fun main(args: Array<String>) {
    runApplication<HomeworkApplication>(*args)
}
