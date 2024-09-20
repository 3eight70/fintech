package ru.fintech.kotlin.categories.entity

import ru.fintech.kotlin.utils.IdentifiableEntity
import ru.fintech.kotlin.utils.annotations.CustomEntity

@CustomEntity(tableName = "categories")
data class Category(
    override val id: Long,
    var slug: String,
    var name: String
) : IdentifiableEntity