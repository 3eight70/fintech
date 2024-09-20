package ru.fintech.kotlin.locations.dto

import kotlinx.serialization.Serializable

@Serializable
class LocationSerializableDto(
    val name: String,
    val slug: String
)