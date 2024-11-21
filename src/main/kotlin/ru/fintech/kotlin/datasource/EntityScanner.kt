package ru.fintech.kotlin.datasource

import org.reflections.Reflections
import ru.fintech.kotlin.utils.annotation.CustomEntity
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

object EntityScanner {
    private val entityStorage = DataSource()

    fun initialize(packageName: String) {
        val reflections = Reflections(packageName)
        val classes = reflections.getTypesAnnotatedWith(CustomEntity::class.java)

        for (c in classes) {
            val kClass = c.kotlin
            if (kClass.hasAnnotation<CustomEntity>()) {
                val annotation = kClass.findAnnotation<CustomEntity>()!!
                entityStorage.createTable(annotation.tableName)
            }
        }
    }

    fun getEntityStorage(): DataSource {
        return entityStorage
    }
}