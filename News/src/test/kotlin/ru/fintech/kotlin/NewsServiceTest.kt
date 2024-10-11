import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import ru.fintech.kotlin.Application
import ru.fintech.kotlin.service.NewsService
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest(classes = [Application::class])
@ActiveProfiles("test")
class NewsServiceTest {

    @Value("\${news-service.rate-limiter.max-concurrent-request}")
    private val maxConcurrentRequests: Int = 2

    @Value("\${news-service.workers.number}")
    private val workers: Int = 2

    @Value("\${news-service.pages}")
    private val totalPages: Int = 20

    private val mockEngine = MockEngine { _ ->
        respond(
            """
        {
          "count": 10,
          "next": "http://example.com",
          "previous": null,
          "results": [
            {
            "id": 51683,
            "publication_date": 1728643216,
            "title": "Северное сияние наблюдали по всей России",
            "place": null,
            "description": "<p>В выходные ждут новую серию «небесного шоу».</p>",
            "site_url": "https://kudago.com/all/news/severnoe-siyanie-nablyudali-po/",
            "favorites_count": 0,
            "comments_count": 0
        },
        {
            "id": 51567,
            "publication_date": 1728639122,
            "title": "«Мне даже сон снился, как я в красном платье поднимаюсь на сцену за Грэмми»: интервью с Baby Cute",
            "place": null,
            "description": "<p>Восходящая звезда лейбла Gazgolder рассказала в интервью KudaGo, что считает своим главным достижением, как реагирует на критику в соцсетях и когда выйдет первый альбом. </p>",
            "site_url": "https://kudago.com/all/news/intervyu-baby-cute/",
            "favorites_count": 0,
            "comments_count": 0
        }
          ]
        }
        """.trimIndent(),
            HttpStatusCode.OK
        )
    }

    private lateinit var semaphore: Semaphore

    @BeforeEach
    fun setup() {
        semaphore = Semaphore(maxConcurrentRequests)
    }

    @AfterEach
    fun after() {
        semaphore.release()
    }

    private val client = HttpClient(mockEngine)

    @Test
    @DisplayName("Количество запросов не должно превышать максимально допустимое")
    fun shouldNotExceedMaxConcurrentRequests() = runBlocking {
        val concurrentRequests = AtomicInteger(0)

        val newsService = NewsService(maxConcurrentRequests, workers, totalPages, client)

        val jobs = List(totalPages) { page ->
            launch(Dispatchers.IO) {
                semaphore.acquire()
                try {
                    val amount = concurrentRequests.incrementAndGet()
                    assertTrue(
                        amount <= maxConcurrentRequests,
                            "Количество одновременно выполняемых запросов равно $amount")
                    newsService.getNewsFromPage(page)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                finally {
                    concurrentRequests.decrementAndGet()
                    semaphore.release()
                }
            }
        }

        jobs.forEach { it.join() }

        assertTrue(
            concurrentRequests.get() <= maxConcurrentRequests,
            "Количество одновременно выполняемых запросов превышает $maxConcurrentRequests"
        )
    }
}