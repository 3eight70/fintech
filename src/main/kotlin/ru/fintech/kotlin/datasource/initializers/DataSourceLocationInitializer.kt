package ru.fintech.kotlin.datasource.initializers

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import ru.fintech.kotlin.datasource.DataSourceInitializer
import ru.fintech.kotlin.datasource.EntityScanner
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import ru.fintech.kotlin.locations.entity.Location
import ru.fintech.kotlin.utils.json.impl.LocationJsonParser
import java.util.*

@Service
class DataSourceLocationInitializer : DataSourceInitializer() {
    private val repository = CustomGenericRepository(Location::class, EntityScanner.getEntityStorage())

    override fun initializeData() {
        val client = HttpClient(CIO)

        log.debug("Начал получать данные по локациям")
        runBlocking {
            try {
                val response = client.get("https://kudago.com/public-api/v1.4/locations").bodyAsText()
                val locations = LocationJsonParser().parse(response)

                for (location in locations) {
                    repository.save(
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