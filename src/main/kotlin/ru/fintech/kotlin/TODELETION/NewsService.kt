package ru.fintech.kotlin.TODELETION

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.slf4j.LoggerFactory
import ru.fintech.kotlin.dto.News
import ru.fintech.kotlin.utils.NewsJsonParser
import java.time.LocalDate
import java.time.ZoneOffset

object NewsService {
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Получение актуальных новостей, отсортированных по дате публикации
     *
     * @param page - страница
     * @param count - количество элементов на странице
     * @param location - локация, откуда брать новости
     * @return news - список новостей
     */
    suspend fun getNews(page: Int = 1, count: Int = 100, location: String = "spb"): List<News> {
        val jsonResponse = sendRequest(page, count, location)
        val news = NewsJsonParser.parseJsonResponse(jsonResponse)
        log.info("Получены новости в размере ${news.size}")

        return news
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
        val client = HttpClient(CIO)

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
        }
    }
}