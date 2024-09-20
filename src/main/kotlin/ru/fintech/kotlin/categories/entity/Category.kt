package ru.fintech.kotlin.categories.entity

import ru.fintech.kotlin.annotations.CustomEntity

@CustomEntity(tableName = "categories")
data class Category(
    val id: Long,
    var slug: String,
    var name: String
)