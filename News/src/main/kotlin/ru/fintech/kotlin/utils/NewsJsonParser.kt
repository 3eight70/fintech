package ru.fintech.kotlin.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.slf4j.LoggerFactory
import ru.fintech.kotlin.dto.News
import ru.fintech.kotlin.dto.NewsResponse

object NewsJsonParser {
    private val log = LoggerFactory.getLogger(this::class.java)
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    /**
     * Парсинг json'а в формат списка новостей
     */
    fun parseJsonResponse(jsonResponse: String): List<News> {
        try {
            return json.decodeFromString<NewsResponse>(jsonResponse).results
        } catch (e: Exception) {
            log.error("Во время парсинга json'а что-то пошло не так", e)
            throw RuntimeException("Что-то пошло не так")
        }
    }
}