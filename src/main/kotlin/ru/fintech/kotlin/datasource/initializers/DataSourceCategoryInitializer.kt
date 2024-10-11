package ru.fintech.kotlin.datasource.initializers

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.stereotype.Service
import ru.fintech.kotlin.category.dto.CategoryDto
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.datasource.DataSourceInitializer
import ru.fintech.kotlin.datasource.EntityScanner
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import ru.fintech.kotlin.utils.annotation.LogExecutionTime
import ru.fintech.kotlin.utils.json.impl.CategoryJsonParser
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Service
class DataSourceCategoryInitializer(
    private val client: HttpClient = HttpClient(CIO),
    private val repository: CustomGenericRepository<Category> = CustomGenericRepository(
        Category::class,
        EntityScanner.getEntityStorage()
    ),
    @Value("\${datasource.initializer.url}")
    private val url: String = "",
    @Qualifier("categoryFixedThreadPool")
    private val fixedThreadPool: ExecutorService,
    @Qualifier("scheduledThreadPool")
    private val scheduledThreadPool: ScheduledExecutorService
) : DataSourceInitializer() {
    @LogExecutionTime
    override fun initializeData() {
        val startTime = Instant.now()
        log.info("Начал получать данные по категориям")
        val categories = runBlocking {
            try {
                val response = client.get("$url/public-api/v1.4/place-categories").bodyAsText()
                CategoryJsonParser().parse(response)
            } catch (e: Exception) {
                log.error("Во время получения категорий что-то пошло не так", e)
                return@runBlocking emptyList<CategoryDto>()
            } finally {
                client.close()
            }
        }

        val tasks = categories.map { category ->
            Runnable {
                try {
                    repository.save(
                        Category(
                            id = Random.nextLong(1, Long.MAX_VALUE),
                            name = category.name,
                            slug = category.slug
                        )
                    )
                    log.debug("Категория: ${category.name} успешно сохранена")
                } catch (e: Exception) {
                    log.error("Ошибка при сохранении категории: ${category.name}", e)
                }
            }
        }

        tasks.forEach { fixedThreadPool.submit(it) }

        fixedThreadPool.shutdown()
        fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)

        log.info("Инициализация категорий завершена за ${Duration.between(startTime, Instant.now()).toMillis()} мс")
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val duration = Duration.ofMinutes(1200)
        scheduledThreadPool.scheduleAtFixedRate({ initializeData() }, 0, duration.toMillis(), TimeUnit.MILLISECONDS)
    }
}