package ru.fintech.kotlin

import ru.fintech.kotlin.utils.NewsSaver
import ru.fintech.kotlin.service.NewsService
import ru.fintech.kotlin.service.NewsService.getMostRatedNews
import ru.fintech.kotlin.utils.formatNews
import ru.fintech.kotlin.utils.renderNewsPage
import java.time.LocalDate

suspend fun main() {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")
    val news = NewsService.getNews(count = 10)
    val mostRatedNews = news.getMostRatedNews(5,
        LocalDate.of(2024, 8, 1)..LocalDate.now())
    //NewsSaver.saveNews("""D:\Programming\asf.csv""", mostRatedNews)
    println("Новости в формате markdown")
    val markdownNews = formatNews(mostRatedNews)
    println(markdownNews)
    println("Новости в формате html")
    val htmlNews = renderNewsPage(mostRatedNews)
    println(htmlNews)
}