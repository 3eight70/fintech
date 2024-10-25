package ru.fintech.kotlin.datasource

import org.springframework.stereotype.Component
import ru.fintech.kotlin.utils.IdentifiableEntity
import java.util.concurrent.ConcurrentHashMap

@Component
class DataSource {
    private val storage = ConcurrentHashMap<String, ConcurrentHashMap<Long, IdentifiableEntity>>()

    fun createTable(tableName: String) {
        storage.computeIfAbsent(tableName) { ConcurrentHashMap() }
    }

    fun put(tableName: String, id: Long, entity: IdentifiableEntity): IdentifiableEntity {
        val tableMap = storage[tableName] ?: throw IllegalArgumentException("Таблица '$tableName' не существует")
        tableMap[id] = entity

        return tableMap[id]!!
    }

    fun get(tableName: String, id: Long): IdentifiableEntity? {
        val tableMap = storage[tableName] ?: throw IllegalArgumentException("Таблица '$tableName' не существует")

        return tableMap[id]
    }

    fun delete(tableName: String, id: Long) {
        val tableMap = storage[tableName] ?: throw IllegalArgumentException("Таблица '$tableName' не существует")

        tableMap.remove(id)
    }

    fun listEntities(tableName: String): List<IdentifiableEntity> {
        return storage[tableName]?.values?.toList() ?: emptyList()
    }
}