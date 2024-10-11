package ru.fintech.kotlin

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.fintech.kotlin.datasource.EntityScanner

@SpringBootApplication(scanBasePackages = ["ru.fintech.kotlin"])
class HomeworkApplication: CommandLineRunner {
    override fun run(vararg args: String?) {
        EntityScanner.initialize("ru.fintech.kotlin")
    }
}

fun main(args: Array<String>) {
    runApplication<HomeworkApplication>(*args)
}
