package ru.fintech.kotlin.category

import ru.fintech.kotlin.category.dto.CategoryDto
import ru.fintech.kotlin.category.dto.RequestCategoryDto

interface CategoryService {
    fun getCategories(): List<CategoryDto>
    fun getCategoryById(id: Long): CategoryDto?
    fun createCategory(createDto: RequestCategoryDto): CategoryDto
    fun updateCategory(id: Long, updateDto: RequestCategoryDto): CategoryDto
    fun deleteCategory(id: Long)
}