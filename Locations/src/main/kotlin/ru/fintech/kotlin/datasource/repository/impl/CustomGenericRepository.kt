package ru.fintech.kotlin.datasource.repository.impl

import ru.fintech.kotlin.datasource.DataSource
import ru.fintech.kotlin.datasource.repository.EntityRepository
import ru.fintech.kotlin.utils.IdentifiableEntity
import ru.fintech.kotlin.utils.annotation.CustomEntity
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class CustomGenericRepository<T : IdentifiableEntity>(
    entityClass: KClass<T>,
    private val storage: DataSource
) : EntityRepository<T> {
    private val tableName: String

    init {
        entityClass.objectInstance
        val annotation = entityClass.findAnnotation<CustomEntity>()
            ?: throw IllegalArgumentException("Класс ${entityClass.simpleName} не имеет аннотации @CustomEntity")
        tableName = annotation.tableName
    }

    override fun save(entity: T): T {
        return storage.put(tableName, entity.id, entity) as T
    }

    override fun findById(id: Long): T? {
        return storage.get(tableName, id) as T?
    }

    override fun findAllById(id: Long): List<T> {
        throw NoSuchMethodException("Функция поиска всех элементов отсутствует")
    }

    override fun findAll(): List<T> {
        return storage.listEntities(tableName) as List<T>
    }

    override fun delete(id: Long) {
        storage.delete(tableName, id)
    }
}