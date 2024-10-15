package ru.fintech.kotlin.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.fintech.kotlin.dto.News
import ru.fintech.kotlin.utils.NewsJsonParser
import ru.fintech.kotlin.utils.NewsSaver
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.concurrent.Semaphore

@Component
class NewsService(
    @Value("\${news-service.rate-limiter.max-concurrent-request}")
    private val maxConcurrentRequests: Int,
    @Value("\${news-service.workers.number}")
    private val numberOfWorkers: Int,
    @Value("\${news-service.pages}")
    private val totalPages: Int,
    private val client: HttpClient = HttpClient(CIO)
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val semaphore = Semaphore(maxConcurrentRequests)

    @Value("\${news-service.test}")
    private val isTest : Boolean = false

    @EventListener(ApplicationReadyEvent::class)
    fun run() {
        if (!isTest) {
            runBlocking {
                log.info("Попытка сохранить новости")
                try {
                    getNews()
                } catch (e: Exception) {
                    log.error("Произошла ошибка", e)
                }
            }
        }
    }

    /**
     * Получение актуальных новостей, отсортированных по дате публикации
     *
     * @param page - страница
     * @param count - количество элементов на странице
     * @param location - локация, откуда брать новости
     * @return news - список новостей
     */
    suspend fun getNewsFromPage(page: Int = 1, count: Int = 100, location: String = "spb"): List<News> {
        val jsonResponse = sendRequest(page, count, location)
        val news = NewsJsonParser.parseJsonResponse(jsonResponse)
        log.info("Получены новости в размере ${news.size}")

        return news
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun getNews() {
        val startTime = Instant.now()
        val newsChannel = Channel<List<News>>()

        val workers = (1..numberOfWorkers).map { workerId ->
            GlobalScope.launch(Dispatchers.IO) {
                for (page in workerId..totalPages step numberOfWorkers) {
                    val news = getNewsFromPage(page)
                    newsChannel.send(news)
                    println("Worker $workerId отправил новости со страницы $page")
                }
            }
        }

        val processor = GlobalScope.launch(Dispatchers.IO) {
            var batchCount = 0
            for (newsBatch in newsChannel) {
                val outputFilePath = "news_batch_$batchCount.csv"
                NewsSaver.saveNews(outputFilePath, newsBatch)
                println("Процессор записал ${newsBatch.size} новостей в $outputFilePath")
                batchCount++
            }
        }

        workers.forEach { it.join() }
        println("Все воркеры завершили работу")

        newsChannel.close()

        processor.join()
        println("Процессор завершил работу")
        println("Время работы параллельной программы в мс: ${Duration.between(startTime, Instant.now()).toMillis()}")
    }

    fun List<News>.getMostRatedNews(count: Int, period: ClosedRange<LocalDate>): List<News> {
        val startTimestamp = period.start.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        val endTimestamp = period.endInclusive.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC)

        return asSequence()
            .filter { news ->
                news.publicationDate in startTimestamp..endTimestamp
            }
            .map { news ->
                news.copy(rating = news.calculateRating())
            }
            .sortedByDescending { it.rating }
            .take(count)
            .toList()
    }

    private suspend fun sendRequest(page: Int = 1, count: Int = 100, location: String = "spb"): String {
        semaphore.acquire()

        log.debug("Начата попытка получения новостей из $location c $page страницы в количестве $count")
        try {
            val jsonResponse: String = client.get("https://kudago.com/public-api/v1.4/news") {
                url {
                    parameters.append(
                        "fields",
                        "id,title,place,description,site_url,favorites_count,comments_count,publication_date"
                    )
                    parameters.append("page_size", count.toString())
                    parameters.append("order_by", "-publication_date")
                    parameters.append("location", location)
                    parameters.append("page", page.toString())
                }
            }.bodyAsText()

            return jsonResponse
        } catch (e: Exception) {
            log.error("Во время получения новостей что-то пошло не так", e)
            throw RuntimeException("Что-то пошло не так")
        } finally {
            client.close()
            semaphore.release()
        }
    }
}