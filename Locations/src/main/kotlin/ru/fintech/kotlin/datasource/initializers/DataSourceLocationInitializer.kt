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
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.datasource.DataSourceInitializer
import ru.fintech.kotlin.datasource.EntityScanner
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import ru.fintech.kotlin.location.dto.LocationDto
import ru.fintech.kotlin.location.dto.LocationSerializableDto
import ru.fintech.kotlin.location.entity.Location
import ru.fintech.kotlin.utils.json.impl.LocationJsonParser
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Service
class DataSourceLocationInitializer(
    private val client: HttpClient = HttpClient(CIO),
    private val repository: CustomGenericRepository<Location> = CustomGenericRepository(
        Location::class,
        EntityScanner.getEntityStorage()
    ),
    @Value("\${datasource.initializer.url}")
    private val url: String = "",
    @Qualifier("locationFixedThreadPool")
    private val fixedThreadPool: ExecutorService,
    @Qualifier("scheduledThreadPool")
    private val scheduledThreadPool: ScheduledExecutorService
) : DataSourceInitializer() {
    override fun initializeData() {
        val startTime = Instant.now()
        log.info("Начал получать данные по локациям")
        val locations = runBlocking {
            try {
                val response = client.get("$url/public-api/v1.4/locations").bodyAsText()
                LocationJsonParser().parse(response)
            } catch (e: Exception) {
                log.error("Во время получения локаций что-то пошло не так", e)
                return@runBlocking emptyList<LocationSerializableDto>()
            } finally {
                client.close()
            }
        }

        val tasks = locations.map { location ->
            Runnable {
                try {
                    repository.save(
                        Location(
                            id = Random().nextLong(1, Long.MAX_VALUE),
                            name = location.name,
                            slug = location.slug
                        )
                    )
                    log.debug("Локация: ${location.name} успешно сохранена")
                } catch (e: Exception) {
                    log.error("Ошибка при сохранении локации: ${location.name}", e)
                }
            }
        }

        tasks.forEach { fixedThreadPool.submit(it) }

        fixedThreadPool.shutdown()
        fixedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)

        log.info("Инициализация локаций завершена за ${Duration.between(startTime, Instant.now()).toMillis()} мс")
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val duration = Duration.ofMinutes(1200)
        scheduledThreadPool.scheduleAtFixedRate({ initializeData() }, 0, duration.toMillis(), TimeUnit.MILLISECONDS)
    }
}