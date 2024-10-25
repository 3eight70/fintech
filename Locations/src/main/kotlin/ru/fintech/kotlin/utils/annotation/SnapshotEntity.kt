package ru.fintech.kotlin.utils.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SnapshotEntity(
    val tableName: String = ""
)

