package ru.fintech.kotlin

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import ru.fintech.kotlin.category.entity.Category
import ru.fintech.kotlin.datasource.DataSource
import ru.fintech.kotlin.datasource.initializers.DataSourceCategoryInitializer
import ru.fintech.kotlin.datasource.repository.impl.CustomGenericRepository
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class DataSourceCategoryTest {
    private val wireMockContainer = GenericContainer(DockerImageName.parse("wiremock/wiremock:2.35.1-1"))
        .withExposedPorts(8080)
        .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)))

    private lateinit var dataSource: DataSource
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
        wireMockContainer.start()
        val wireMockPort = wireMockContainer.getMappedPort(8080)
        val wireMockHost = wireMockContainer.host
        configureFor(wireMockHost, wireMockPort)
        dataSource = DataSource()
        dataSource.createTable("categories")
        repository = CustomGenericRepository(Category::class, dataSource)
        initializer = DataSourceCategoryInitializer(
            repository = repository,
            url = "http://$wireMockHost:$wireMockPort",
            fixedThreadPool = fixedThreadPool,
            scheduledThreadPool = fixedScheduledPool
        )
    }

    @AfterEach
    fun tearDown() {
        if (wireMockContainer.isRunning) {
            wireMockContainer.stop()
        }
    }

    @Test
    @DisplayName("При получении данных от сервера должен сохранить")
    fun shouldSaveMockData() {
        stubFor(
            get("/public-api/v1.4/place-categories")
                .willReturn(
                    okJson(
                        """
                    [
                        {"id": "1", "name": "spb", "slug": "spb"},
                        {"id": "2", "name": "tsk", "slug": "tsk"}
                    ]
                    """.trimIndent()
                    )
                )
        )

        initializer.initializeData()

        val categories = repository.findAll()

        Assertions.assertEquals(categories.size, 2)

        //Конкретно в этой ситуации придется проверить так, потому что под капотом id генерятся автоматически,
        //а последовательность гарантировать нельзя
        categories.forEach { category ->
            Assertions.assertTrue(category.name == "spb" || category.name == "tsk")
        }
    }

    @Test
    @DisplayName("Должен выкидывать Runtime exception, если при попытке получения данных произошла ошибка")
    fun shouldThrowExceptionWhenConnectionFailed() {
        wireMockContainer.stop()

        val exception = assertThrows<RuntimeException> {
            initializer.initializeData()
        }

        Assertions.assertEquals(exception.message, "Что-то пошло не так")
    }

    @Test
    @DisplayName("Должен выкидывать Runtime exception, если при попытке парсинга полученного сообщения произошла ошибка")
    fun shouldThrowExceptionWhenParsingFailed() {
        stubFor(
            get("/public-api/v1.4/place-categories")
                .willReturn(
                    okJson(
                        """{"blablalb":  "dasvas"}"""
                    )
                )
        )

        val exception = assertThrows<RuntimeException> {
            initializer.initializeData()
        }

        Assertions.assertEquals(exception.message, "Что-то пошло не так")
    }
}