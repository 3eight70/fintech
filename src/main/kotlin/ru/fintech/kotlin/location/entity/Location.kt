package ru.fintech.kotlin.location.entity

import ru.fintech.kotlin.utils.IdentifiableEntity
import ru.fintech.kotlin.utils.annotation.CustomEntity

@CustomEntity(tableName = "locations")
class Location (
    override val id: Long,
    var name: String,
    var slug: String
) : IdentifiableEntity