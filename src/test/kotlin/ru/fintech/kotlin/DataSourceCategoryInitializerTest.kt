package ru.fintech.kotlin

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.boot.test.context.SpringBootTest
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.datasource.initializers.DataSourceCategoryInitializer
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import java.net.ConnectException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DataSourceCategoryInitializerTest {
    private lateinit var initializer: DataSourceCategoryInitializer
    private lateinit var repository: CustomGenericRepository<Category>

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

            initializer = DataSourceCategoryInitializer(client, repository)

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
            initializer = DataSourceCategoryInitializer(client, repository)

            assertThrows<RuntimeException> {
                initializer.initializeData()
            }
        }
    }

    @Test
    @DisplayName("Должен выкидывать Runtime exception, если пришел совсем некорректный json")
    fun shouldHandleParsingErrorWhenClientReturnInvalidJson() {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "bla bla bla",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(mockEngine)
        initializer = DataSourceCategoryInitializer(client, repository)

        assertThrows<RuntimeException> {
            initializer.initializeData()
        }
    }

    @Test
    @DisplayName("Должен выкидывать Runtime exception, если произошла ошибка соединения")
    fun shouldHandleNetworkError() {
        runBlocking {
            val mockEngine = MockEngine { _ ->
                throw ConnectException()
            }
            val client = HttpClient(mockEngine)
            initializer = DataSourceCategoryInitializer(client, repository)

            assertThrows<RuntimeException> {
                initializer.initializeData()
            }
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
            initializer = DataSourceCategoryInitializer(client, repository)

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
            initializer = DataSourceCategoryInitializer(client, repository)

            whenever(repository.save(any<Category>())).thenThrow(RuntimeException("Save failed"))

            assertThrows<RuntimeException> {
                initializer.initializeData()
            }
        }
    }
}