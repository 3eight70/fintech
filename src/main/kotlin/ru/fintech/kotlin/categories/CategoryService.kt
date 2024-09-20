package ru.fintech.kotlin.categories

import ru.fintech.kotlin.categories.dto.CategoryDto
import ru.fintech.kotlin.categories.dto.RequestCategoryDto

interface CategoryService {
    fun getCategories(): List<CategoryDto>
    fun getCategoryById(id: Long): CategoryDto?
    fun createCategory(createDto: RequestCategoryDto): CategoryDto
    fun updateCategory(id: Long, updateDto: RequestCategoryDto): CategoryDto
    fun deleteCategory(id: Long)
}