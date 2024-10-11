package ru.fintech.kotlin.datasource.repository

interface EntityRepository<T> {
    fun save(entity: T): T
    fun findById(id: Long): T?
    fun findAll(): List<T>
    fun delete(id: Long)
}