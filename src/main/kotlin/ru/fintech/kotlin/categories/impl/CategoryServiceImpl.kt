package ru.fintech.kotlin.categories.impl

import org.springframework.stereotype.Service
import ru.fintech.kotlin.categories.CategoryService
import ru.fintech.kotlin.categories.dto.CategoryDto
import ru.fintech.kotlin.categories.dto.RequestCategoryDto
import ru.fintech.kotlin.categories.entity.Category
import ru.fintech.kotlin.datasource.EntityScanner
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository

@Service
class CategoryServiceImpl: CategoryService {
    private val categoryRepository = CustomGenericRepository(Category::class, EntityScanner.getEntityStorage())

    override fun getCategories(): List<CategoryDto> {
        TODO("Not yet implemented")
    }

    override fun getCategoryById(id: Long): CategoryDto {
        TODO("Not yet implemented")
    }

    override fun createCategory(createDto: RequestCategoryDto): CategoryDto {
        TODO("Not yet implemented")
    }

    override fun updateCategory(id: Long, updateDto: RequestCategoryDto): CategoryDto {
        TODO("Not yet implemented")
    }

    override fun deleteCategory(id: Long) {
        TODO("Not yet implemented")
    }

}