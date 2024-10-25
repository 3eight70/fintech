package ru.fintech.kotlin.datasource.initializers

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.datasource.DataSourceInitializer
import ru.fintech.kotlin.datasource.EntityScanner
import ru.fintech.kotlin.datasource.observer.GenericObserver
import ru.fintech.kotlin.datasource.observer.Subject
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import ru.fintech.kotlin.utils.json.impl.CategoryJsonParser
import kotlin.random.Random

@Service
class DataSourceCategoryInitializer(
    private val client: HttpClient = HttpClient(CIO),
    repository: CustomGenericRepository<Category> = CustomGenericRepository(
        Category::class,
        EntityScanner.getEntityStorage()
    )
) : DataSourceInitializer() {
    private val subject = Subject<Category>()
    private val observer = GenericObserver(repository)

    init {
        subject.attach(observer)
    }

    override fun initializeData() {
        log.info("Начал получать данные по категориям")
        runBlocking {
            try {
                val response = client.get("https://kudago.com/public-api/v1.4/place-categories").bodyAsText()
                val categories = CategoryJsonParser().parse(response)

                for (category in categories) {
                    subject.notifyObservers(
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