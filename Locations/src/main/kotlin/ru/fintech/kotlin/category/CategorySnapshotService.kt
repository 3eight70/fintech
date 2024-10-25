package ru.fintech.kotlin.category

import org.springframework.stereotype.Service
import ru.fintech.kotlin.category.entity.CategorySnapshot
import ru.fintech.kotlin.datasource.SnapshotScanner
import ru.fintech.kotlin.datasource.repository.impl.CustomSnapshotRepository


interface CategorySnapshotService {
    fun getCategorySnapshotHistory(id: Long): List<CategorySnapshot>
}

@Service
class CategorySnapshotServiceImpl(
    private val categorySnapshotRepository: CustomSnapshotRepository<CategorySnapshot> = CustomSnapshotRepository(
        CategorySnapshot::class,
        SnapshotScanner.getEntityStorage()
    )
) : CategorySnapshotService {
    override fun getCategorySnapshotHistory(id: Long) =
        categorySnapshotRepository.findAllById(id)
}