package ru.fintech.kotlin.datasource.observer

import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import ru.fintech.kotlin.utils.IdentifiableEntity

class GenericObserver<T : IdentifiableEntity>(
    private val repository: CustomGenericRepository<T>
) : Observer<T> {
    override fun update(entity: T) {
        repository.save(entity)
    }
}