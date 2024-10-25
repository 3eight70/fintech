package ru.fintech.kotlin.datasource.observer

interface Observer<T> {
    fun update(entity: T)
}