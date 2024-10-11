package ru.fintech.tinkoff.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.slf4j.LoggerFactory
import ru.fintech.tinkoff.dto.ApiResponse
import ru.fintech.tinkoff.dto.KudaGoEvent

object EventJsonParser {
    private val serializer = ApiResponse.serializer()
    private val log = LoggerFactory.getLogger(javaClass)

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    fun parse(jsonString: String): List<KudaGoEvent> =
        try {
            val response = json.decodeFromString(serializer, jsonString)
            response.results
        } catch (e: Exception) {
            log.error("Во время парсинга json'а что-то пошло не так", e)
            throw RuntimeException("Что-то пошло не так")
        }
}