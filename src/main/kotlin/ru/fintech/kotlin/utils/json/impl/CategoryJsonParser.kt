package ru.fintech.kotlin.utils.json.impl

import ru.fintech.kotlin.categories.dto.CategoryDto
import ru.fintech.kotlin.utils.json.JsonParser

class CategoryJsonParser : JsonParser<CategoryDto>(CategoryDto.serializer())