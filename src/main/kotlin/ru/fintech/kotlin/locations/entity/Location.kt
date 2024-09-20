package ru.fintech.kotlin.locations.entity

import ru.fintech.kotlin.utils.IdentifiableEntity
import ru.fintech.kotlin.utils.annotations.CustomEntity

@CustomEntity(tableName = "locations")
class Location (
    override val id: Long,
    var name: String,
    var slug: String
) : IdentifiableEntity