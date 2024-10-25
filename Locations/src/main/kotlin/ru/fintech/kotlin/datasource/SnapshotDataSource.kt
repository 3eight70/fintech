package ru.fintech.kotlin.datasource

import ru.fintech.kotlin.utils.IdentifiableEntity
import java.util.concurrent.ConcurrentHashMap

class SnapshotDataSource {
    private val storage = ConcurrentHashMap<String, ConcurrentHashMap<Long, MutableList<IdentifiableEntity>>>()

    fun createTable(tableName: String) {
        storage.computeIfAbsent(tableName) { ConcurrentHashMap() }
    }

    fun put(tableName: String, id: Long, entity: IdentifiableEntity): IdentifiableEntity {
        val tableMap = storage[tableName] ?: throw IllegalArgumentException("Таблица '$tableName' не существует")
        if (tableMap[id] == null)
            tableMap[id] = mutableListOf()

        tableMap[id]!!.add(entity)

        return tableMap[id]!!.last()
    }

    fun get(tableName: String, id: Long): List<IdentifiableEntity>? {
        val tableMap = storage[tableName] ?: throw IllegalArgumentException("Таблица '$tableName' не существует")

        return tableMap[id]
    }

    fun getLast(tableName: String, id: Long): IdentifiableEntity {
        val tableMap = storage[tableName] ?: throw IllegalArgumentException("Таблица '$tableName' не существует")

        return tableMap[id]!!.last()
    }
}