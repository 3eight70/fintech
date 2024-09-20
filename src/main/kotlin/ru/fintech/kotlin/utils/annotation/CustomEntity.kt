package ru.fintech.kotlin.utils.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomEntity (
    val tableName: String = ""
)
