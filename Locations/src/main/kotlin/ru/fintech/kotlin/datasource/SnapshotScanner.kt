package ru.fintech.kotlin.datasource

import org.reflections.Reflections
import ru.fintech.kotlin.utils.annotation.SnapshotEntity
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

object SnapshotScanner : Scanner{
    private val entityStorage = SnapshotDataSource()

    override fun initialize(packageName: String) {
        val reflections = Reflections(packageName)
        val classes = reflections.getTypesAnnotatedWith(SnapshotEntity::class.java)

        for (c in classes) {
            val kClass = c.kotlin
            if (kClass.hasAnnotation<SnapshotEntity>()) {
                val annotation = kClass.findAnnotation<SnapshotEntity>()!!
                entityStorage.createTable(annotation.tableName)
            }
        }
    }

    fun getEntityStorage(): SnapshotDataSource {
        return entityStorage
    }
}