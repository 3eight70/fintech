package ru.fintech.kotlin.datasource.initializers

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import ru.fintech.kotlin.categories.entity.Category
import ru.fintech.kotlin.datasource.DataSourceInitializer
import ru.fintech.kotlin.datasource.EntityScanner
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import ru.fintech.kotlin.utils.json.impl.CategoryJsonParser
import kotlin.random.Random

@Service
class DataSourceCategoryInitializer : DataSourceInitializer() {
    private val repository = CustomGenericRepository(Category::class, EntityScanner.getEntityStorage())

    override fun initializeData() {
        val client = HttpClient(CIO)

        log.debug("Начал получать данные по категориям")
        runBlocking {
            try {
                val response = client.get("https://kudago.com/public-api/v1.4/place-categories").bodyAsText()
                val categories = CategoryJsonParser().parse(response)

                for (category in categories) {
                    repository.save(
                        Category(
                            id = Random.nextLong(1, Long.MAX_VALUE),
                            name = category.name,
                            slug = category.slug
                        )
                    )
                }
            } catch (e: Exception) {
                log.error("Во время получения категорий что-то пошло не так", e)
                throw RuntimeException("Что-то пошло не так")
            } finally {
                client.close()
            }
        }
    }
}