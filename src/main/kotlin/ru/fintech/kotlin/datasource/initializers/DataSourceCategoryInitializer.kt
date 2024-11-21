package ru.fintech.kotlin.datasource.initializers

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.datasource.DataSourceInitializer
import ru.fintech.kotlin.datasource.EntityScanner
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import ru.fintech.kotlin.utils.json.impl.CategoryJsonParser
import kotlin.random.Random

@Service
class DataSourceCategoryInitializer(
    private val client: HttpClient = HttpClient(CIO),
    private val repository: CustomGenericRepository<Category> = CustomGenericRepository(
        Category::class,
        EntityScanner.getEntityStorage()
    ),
    @Value("\${datasource.initializer.url}")
    private val url: String = ""
) : DataSourceInitializer() {
    override fun initializeData() {
        log.info("Начал получать данные по категориям")
        runBlocking {
            try {
                val response = client.get("$url/public-api/v1.4/place-categories").bodyAsText()
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