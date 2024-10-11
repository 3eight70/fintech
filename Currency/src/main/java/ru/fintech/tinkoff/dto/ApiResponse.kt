package ru.fintech.tinkoff.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<KudaGoEvent>
)