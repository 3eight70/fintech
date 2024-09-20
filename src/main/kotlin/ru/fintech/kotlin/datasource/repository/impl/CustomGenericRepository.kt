package ru.fintech.kotlin.datasource.repository.impl

import ru.fintech.kotlin.annotations.CustomEntity
import ru.fintech.kotlin.datasource.DataSource
import ru.fintech.kotlin.datasource.repository.EntityRepository
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class CustomGenericRepository<T : Any>(
    entityClass: KClass<T>,
    private val storage: DataSource
) : EntityRepository<T> {
    private val tableName: String

    init {
        val annotation = entityClass.findAnnotation<CustomEntity>()
            ?: throw IllegalArgumentException("Класс ${entityClass.simpleName} не имеет аннотации @CustomEntity")
        tableName = annotation.tableName
    }

    override fun save(entity: T): T {
        val id = entity.hashCode().toLong()
        return storage.put(tableName, id, entity) as T
    }

    override fun findById(id: Long): T? {
        return storage.get(tableName, id) as T?
    }

    override fun findAll(): List<T> {
        return storage.listEntities(tableName) as List<T>
    }

    override fun delete(id: Long) {
        storage.delete(tableName, id)
    }
}