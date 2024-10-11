package ru.fintech.tinkoff.dto

import kotlinx.serialization.Serializable

@Serializable
class KudaGoEvent (
    val title: String,
    val price: String,
    val isFree: Boolean
)