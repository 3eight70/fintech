package ru.fintech.kotlin.utils

import ru.fintech.kotlin.dto.News
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NewsPrinter {
    private val builder = StringBuilder()

    fun header(level: Int, content: String) {
        builder.append("#".repeat(level))
        builder.append(" $content\n")
    }

    fun text(content: String) {
        builder.append(content).append("\n")
    }

    fun bold(content: String) {
        builder.append("**$content**")
    }

    fun underlined(content: String) {
        builder.append("__${content}__")
    }

    fun getBold(content: String): String {
        return "**$content**"
    }

    fun getUnderlined(content: String): String {
        return "__${content}__"
    }

    fun link(link: String, text: String) {
        builder.append("[$text]($link)").append("\n")
    }

    override fun toString(): String = builder.toString()
}

fun newsPrinter(block: NewsPrinter.() -> Unit): String {
    val printer = NewsPrinter()
    printer.block()
    return printer.toString()
}

fun formatNews(newsList: List<News>): String {
    return newsPrinter {
        header(level = 1, content = "Внимание-внимание")

        newsList.forEach { news ->
            header(level = 2, content = news.title)
            text("Дата публикации: ${formatDate(news.publicationDate)}")
            text("Место происшествия: ${news.place ?: "Неизвестно"}")
            text("Описание: ${news.description ?: "отсутствует"}")
            link(news.siteUrl ?: "Ссылка на ресурс отсутствует", "Прочитать подробнее")
            text(getBold("Рейтинг: ${news.rating}"))
            text(getUnderlined("Лайки: ${news.favoritesCount}"))
            text(getUnderlined("Комментарии: ${news.commentsCount}"))
            bold("Конец статьи")
            text("\n")
        }
    }
}

private fun formatDate(epochMilli: Long): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.ofEpochDay(epochMilli / (24 * 60 * 60 * 1000))
    return date.format(dateTimeFormatter)
}