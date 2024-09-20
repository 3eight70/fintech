package ru.fintech.kotlin.location.dto

import kotlinx.serialization.Serializable

@Serializable
class LocationSerializableDto(
    val name: String,
    val slug: String
)