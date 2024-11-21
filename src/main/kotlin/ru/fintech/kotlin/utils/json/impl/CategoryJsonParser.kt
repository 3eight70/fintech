package ru.fintech.kotlin.utils.json.impl

import ru.fintech.kotlin.category.dto.CategoryDto
import ru.fintech.kotlin.utils.json.JsonParser

class CategoryJsonParser : JsonParser<CategoryDto>(CategoryDto.serializer())