package ru.fintech.kotlin.dto

import kotlinx.serialization.Serializable
import kotlin.math.exp

/**
 * Модель для парсинга новостей
 *
 * @property id - идентификатор новости
 * @property title - заголовок новости
 * @property publicationDate - дата публикации новости в миллисекундах
 * @property place - место, где произошло событие
 * @property description - описание новости
 * @property siteUrl - ссылка на страницу новости на сайте KudaGo
 * @property favoritesCount - число пользователей, добавивших новость в избранное
 * @property commentsCount - число комментариев
 * @property rating - рейтинг новости
 */
@Serializable
data class News(
    val id: Long,
    val title: String,
    val publicationDate: Long,
    val place: Place?,
    val description: String?,
    val siteUrl: String?,
    val favoritesCount: Long,
    val commentsCount: Long,
    var rating: Double = 0.0
) {
    fun calculateRating(): Double {
        this.rating = 1 / (1 + exp((-(favoritesCount / (commentsCount + 1))).toDouble()))

        return rating
    }

    fun toCsvRow(): String {
        return "$id,\"$title\",$publicationDate,\"$place\",\"$description\",\"$siteUrl\",$favoritesCount,$commentsCount,$rating"
    }
}