package ru.fintech.kotlin

import ru.fintech.kotlin.utils.NewsSaver
import ru.fintech.kotlin.service.NewsService
import ru.fintech.kotlin.service.NewsService.getMostRatedNews
import ru.fintech.kotlin.utils.formatNews
import java.time.LocalDate

suspend fun main() {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")
    val news = NewsService.getNews(count = 10)
    val mostRatedNews = news.getMostRatedNews(5,
        LocalDate.of(2024, 8, 1)..LocalDate.of(2024, 9, 14))
    //NewsSaver.saveNews("""D:\Programming\asf.csv""", mostRatedNews)

    val formattedNews = formatNews(mostRatedNews)
    println(formattedNews)
}