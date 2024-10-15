package ru.fintech.kotlin

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.config.ExecutorsProperties
import ru.fintech.kotlin.datasource.initializers.DataSourceCategoryInitializer
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import java.net.ConnectException
import java.time.Duration
import java.util.concurrent.Executors

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DataSourceCategoryInitializerTest {
    private lateinit var initializer: DataSourceCategoryInitializer
    private lateinit var repository: CustomGenericRepository<Category>
    private val properties = ExecutorsProperties(
        duration = Duration.ofMinutes(1000)
    )
    private val fixedThreadPool = Executors.newFixedThreadPool(properties.fixedPoolSize).apply {
        Thread.currentThread().name = "LocationFixedThreadPool"
    }
    private val fixedScheduledPool = Executors.newScheduledThreadPool(properties.scheduledPoolSize).apply {
        Thread.currentThread().name = "ScheduledThreadPool"
    }

    @BeforeEach
    fun setup() {
        repository = mock()
    }

    @Test
    @DisplayName("При инициализации должен сохранить всю полученную информацию")
    fun shouldSaveDataWhenInitialize() {
        runBlocking {
            val mockEngine = MockEngine { _ ->
                respond(
                    content = """[{"id": "124124521", "name": "Category", "slug": "category"}]""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val client = HttpClient(mockEngine)

            initializer = DataSourceCategoryInitializer(
                client,
                repository,
                "http://MOCKasdasdasd.ru",
                fixedThreadPool,
                fixedScheduledPool,
                properties
            )

            initializer.initializeData()

            verify(repository, times(1)).save(any<Category>())
        }
    }

    @Test
    @DisplayName("Должен выкидывать Runtime exception, если во время получения данных что-то пошло не так")
    fun shouldThrowRuntimeExceptionWhenSomethingDuringRequestGoneWrong() {
        runBlocking {
            val mockEngine = MockEngine { _ ->
                respond(
                    //Отсутствует поле id
                    content = """[{"name": "Category", "slug": "category"}]""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val client = HttpClient(mockEngine)
            initializer = DataSourceCategoryInitializer(
                client,
                repository,
                "http://MOCKasdasdasd.ru",
                fixedThreadPool,
                fixedScheduledPool,
                properties
            )

            val exception = assertThrows<RuntimeException> {
                initializer.initializeData()
            }

            Assertions.assertEquals(exception.message, "Что-то пошло не так")
        }
    }

    @Test
    @DisplayName("Должен выкидывать Runtime exception, если пришел совсем некорректный json")
    fun shouldHandleParsingErrorWhenClientReturnInvalidJson() {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "[bla bla bla]",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(mockEngine)
        initializer = DataSourceCategoryInitializer(
            client,
            repository,
            "http://MOCKasdasdasd.ru",
            fixedThreadPool,
            fixedScheduledPool,
            properties
        )

        val exception = assertThrows<RuntimeException> {
            initializer.initializeData()
        }

        Assertions.assertEquals(exception.message, "Что-то пошло не так")
    }

    @Test
    @DisplayName("Должен выкидывать Runtime exception, если произошла ошибка соединения")
    fun shouldHandleNetworkError() {
        runBlocking {
            val mockEngine = MockEngine { _ ->
                throw ConnectException()
            }
            val client = HttpClient(mockEngine)
            initializer = DataSourceCategoryInitializer(
                client,
                repository,
                "http://MOCKasdasdasd.ru",
                fixedThreadPool,
                fixedScheduledPool,
                properties
            )

            val exception = assertThrows<RuntimeException> {
                initializer.initializeData()
            }

            Assertions.assertEquals(exception.message, "Что-то пошло не так")
        }
    }

    @Test
    @DisplayName("Должен корректно обрабатывать пустой ответ от API")
    fun shouldHandleEmptyResponse() {
        runBlocking {
            val mockEngine = MockEngine { _ ->
                respond(
                    content = "[]",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val client = HttpClient(mockEngine)
            initializer = DataSourceCategoryInitializer(
                client,
                repository,
                "http://MOCKasdasdasd.ru",
                fixedThreadPool,
                fixedScheduledPool,
                properties
            )

            initializer.initializeData()

            verify(repository, never()).save(any<Category>())
        }
    }

    @Test
    @DisplayName("Должен выкидывать Runtime exception, если произошла ошибка при сохранении")
    fun shouldThrowRuntimeExceptionWhenSaveFails() {
        runBlocking {
            val mockEngine = MockEngine { _ ->
                respond(
                    content = """[{"id": "124124521", "name": "Category", "slug": "category"}]""",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val client = HttpClient(mockEngine)
            initializer = DataSourceCategoryInitializer(
                client,
                repository,
                "http://MOCKasdasdasd.ru",
                fixedThreadPool,
                fixedScheduledPool,
                properties
            )

            whenever(repository.save(any<Category>())).thenThrow(RuntimeException("Что-то пошло не так"))

            val exception = assertThrows<RuntimeException> {
                initializer.initializeData()
            }

            Assertions.assertEquals(exception.message, "Что-то пошло не так")
        }
    }
}