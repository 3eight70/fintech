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
import ru.fintech.kotlin.location.entity.Location
import ru.fintech.kotlin.utils.json.impl.LocationJsonParser
import java.util.*

@Service
class DataSourceLocationInitializer(
    private val client: HttpClient = HttpClient(CIO),
    private val repository: CustomGenericRepository<Location> = CustomGenericRepository(
        Location::class,
        EntityScanner.getEntityStorage()
    )
) : DataSourceInitializer() {
    private val subject = Subject<Location>()
    private val observer = GenericObserver(repository)

    init {
        subject.attach(observer)
    }

    override fun initializeData() {
        log.info("Начал получать данные по локациям")
        runBlocking {
            try {
                val response = client.get("https://kudago.com/public-api/v1.4/locations").bodyAsText()
                val locations = LocationJsonParser().parse(response)

                for (location in locations) {
                    subject.notifyObservers(
                        Location(
                            id = Random().nextLong(1, Long.MAX_VALUE),
                            name = location.name,
                            slug = location.slug
                        )
                    )
                }
            } catch (e: Exception) {
                log.error("Во время получения локаций что-то пошло не так", e)
                throw RuntimeException("Что-то пошло не так")
            } finally {
                client.close()
            }
        }
    }
}