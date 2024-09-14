package ru.fintech.kotlin.utils

import org.slf4j.LoggerFactory
import ru.fintech.kotlin.dto.News
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object NewsSaver {
    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * Сохраняет новости в .csv файл
     * @param path - путь, с указанием названия файла, пример: D:\example.csv
     * @param news - сохраняемые новости
     */
    fun saveNews(path: String, news: Collection<News>) {
        log.debug("Сохраняем новости в файл")
        val validPath = if (path.endsWith(".csv")) path else "$path.csv"

        val file = File(validPath)

        if (file.exists()) {
            log.error("Файл по указанному пути $validPath, уже существует")
            throw IllegalArgumentException("По указанному пути уже существует файл")
        }

        FileOutputStream(validPath).use { fos ->
            OutputStreamWriter(fos, StandardCharsets.UTF_8).use { osw ->
                osw.write("\uFEFF")

                osw.write("id,title,publicationDate,place,description,siteUrl,favoritesCount,commentsCount,rating\n")

                news.forEach { newsItem ->
                    newsItem.calculateRating()
                    osw.write("${newsItem.toCsvRow()}\n")
                }
            }
        }

        log.info("Новости успешно сохранены в файле: $validPath")
    }
}
