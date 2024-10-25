package ru.fintech.kotlin.datasource.repository.impl

import ru.fintech.kotlin.datasource.SnapshotDataSource
import ru.fintech.kotlin.datasource.repository.EntityRepository
import ru.fintech.kotlin.utils.IdentifiableEntity
import ru.fintech.kotlin.utils.annotation.SnapshotEntity
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class CustomSnapshotRepository<T : IdentifiableEntity>(
    entityClass: KClass<T>,
    private val storage: SnapshotDataSource
) : EntityRepository<T> {
    private val tableName: String

    init {
        entityClass.objectInstance
        val annotation = entityClass.findAnnotation<SnapshotEntity>()
            ?: throw IllegalArgumentException("Класс ${entityClass.simpleName} не имеет аннотации @CustomEntity")
        tableName = annotation.tableName
    }

    override fun save(entity: T): T {
        return storage.put(tableName, entity.id, entity) as T
    }

    override fun findById(id: Long): T? {
        return storage.getLast(tableName, id) as T
    }

    override fun findAllById(id: Long): List<T> {
        return storage.get(tableName, id) as List<T>
    }

    override fun findAll(): List<T> {
        return listOf()
    }

    override fun delete(id: Long) {
        throw NoSuchMethodException("Функция удаления отсутствует")
    }
}