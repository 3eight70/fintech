package ru.fintech.kotlin.categories.mapper

import ru.fintech.kotlin.categories.dto.CategoryDto
import ru.fintech.kotlin.categories.entity.Category

object CategoryMapper {
    fun entityToDto(entity: Category): CategoryDto {
        return CategoryDto(
            id = entity.id,
            name = entity.name,
            slug = entity.slug
        )
    }
}