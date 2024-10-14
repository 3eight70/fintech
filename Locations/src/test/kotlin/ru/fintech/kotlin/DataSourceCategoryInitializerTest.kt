package ru.fintech.kotlin

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.mockito.kotlin.*
import org.springframework.boot.test.context.SpringBootTest
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.datasource.initializers.DataSourceCategoryInitializer
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import java.net.ConnectException
import java.util.concurrent.Executors

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DataSourceCategoryInitializerTest {
    private lateinit var initializer: DataSourceCategoryInitializer
    private lateinit var repository: CustomGenericRepository<Category>
    private val fixedThreadPool = Executors.newFixedThreadPool(5).apply {
        Thread.currentThread().name = "LocationFixedThreadPool"
    }
    private val fixedScheduledPool = Executors.newScheduledThreadPool(2).apply {
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
                fixedScheduledPool
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
                fixedScheduledPool
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
            fixedScheduledPool
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
                fixedScheduledPool
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
                fixedScheduledPool
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
                fixedScheduledPool
            )

            whenever(repository.save(any<Category>())).thenThrow(RuntimeException("Что-то пошло не так"))

            val exception = assertThrows<RuntimeException> {
                initializer.initializeData()
            }

            Assertions.assertEquals(exception.message, "Что-то пошло не так")
        }
    }
}