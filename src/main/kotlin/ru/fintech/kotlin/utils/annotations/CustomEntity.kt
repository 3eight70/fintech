package ru.fintech.kotlin.utils.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomEntity (
    val tableName: String = ""
)
