package ru.fintech.kotlin.locations.entity

import ru.fintech.kotlin.annotations.CustomEntity

@CustomEntity(tableName = "locations")
class Location (
    val id: Long,
    var name: String,
    var slug: String
)