package ru.fintech.kotlin.category.entity

import ru.fintech.kotlin.utils.IdentifiableEntity
import ru.fintech.kotlin.utils.annotation.CustomEntity

@CustomEntity(tableName = "categories")
data class Category(
    override val id: Long,
    var slug: String,
    var name: String
) : IdentifiableEntity