package ru.fintech.kotlin.utils.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.slf4j.LoggerFactory

abstract class JsonParser<T>(private val serializer: KSerializer<T>) {
    private val log = LoggerFactory.getLogger(javaClass)

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        namingStrategy = JsonNamingStrategy.SnakeCase
    }

    fun parse(jsonString: String): List<T> {
        try {
            return json.decodeFromString(ListSerializer(serializer), jsonString)
        } catch (e: Exception) {
            log.error("Во время парсинга json'а что-то пошло не так", e)
            throw RuntimeException("Что-то пошло не так")
        }
    }
}