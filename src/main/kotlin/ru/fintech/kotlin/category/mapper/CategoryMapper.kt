package ru.fintech.kotlin.category.mapper

import ru.fintech.kotlin.category.dto.CategoryDto
import ru.fintech.kotlin.category.entity.Category

object CategoryMapper {
    fun entityToDto(entity: Category): CategoryDto {
        return CategoryDto(
            id = entity.id,
            name = entity.name,
            slug = entity.slug
        )
    }
}