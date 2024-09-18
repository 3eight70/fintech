package ru.fintech.kotlin.dto

import kotlinx.serialization.Serializable

/**
 * Модель для сериализации результатов от сайта с новостями
 */
@Serializable
class NewsResponse (
    val count: Long?,
    val next: String?,
    val previous: String?,
    val results: List<News>
)