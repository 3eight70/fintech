package ru.fintech.kotlin.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomEntity (
    val tableName: String = ""
)
