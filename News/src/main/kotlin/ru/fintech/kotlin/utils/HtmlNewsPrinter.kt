package ru.fintech.kotlin.utils

import ru.fintech.kotlin.dto.News
import java.text.SimpleDateFormat
import java.util.*

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

@DslMarker
annotation class NewsTagMarker

@NewsTagMarker
abstract class Tag(val name: String) : Element {
    val children = arrayListOf<Element>()
    val attributes = hashMapOf<String, String>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent<$name${renderAttributes()}>\n")
        for (c in children) {
            c.render(builder, indent + "  ")
        }
        builder.append("$indent</$name>\n")
    }

    private fun renderAttributes(): String {
        val builder = StringBuilder()
        for ((attr, value) in attributes) {
            builder.append(" $attr=\"$value\"")
        }
        return builder.toString()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

abstract class TagWithText(name: String) : Tag(name) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

class HTML : TagWithText("html") {
    fun head(init: Head.() -> Unit) = initTag(Head(), init)
    fun body(init: Body.() -> Unit) = initTag(Body(), init)
}

class Head : TagWithText("head") {
    fun title(init: Title.() -> Unit) = initTag(Title(), init)
}

class Title : TagWithText("title")

abstract class BodyTag(name: String) : TagWithText(name) {
    fun h1(init: H1.() -> Unit) = initTag(H1(), init)
    fun p(init: P.() -> Unit) = initTag(P(), init)
    fun a(href: String, init: A.() -> Unit) {
        val a = initTag(A(), init)
        a.href = href
    }

    fun newsBlock(news: News, init: NewsBlock.() -> Unit) = initTag(NewsBlock(news), init)
}

class Body : BodyTag("body")
class H1 : BodyTag("h1")
class P : BodyTag("p")

class A : BodyTag("a") {
    var href: String
        get() = attributes["href"]!!
        set(value) {
            attributes["href"] = value
        }
}

class NewsBlock(val news: News) : TagWithText("div") {
    override fun render(builder: StringBuilder, indent: String) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = Date(news.publicationDate)
        val formattedDate = dateFormat.format(date)

        builder.append("$indent<div class=\"news-block\">\n")
        builder.append("$indent  <h2>${news.title}</h2>\n")
        builder.append("$indent  <p><strong>Дата события:</strong> $formattedDate</p>\n")

        if (news.place != null) {
            builder.append("$indent  <p><strong>Идентификатор места:</strong> ${news.place.id}</p>\n")
        }

        if (!news.description.isNullOrEmpty()) {
            builder.append("$indent  <p>${news.description}</p>\n")
        }

        if (!news.siteUrl.isNullOrEmpty()) {
            builder.append("$indent  <p><a href=\"${news.siteUrl}\">Узнать больше...</a></p>\n")
        }

        builder.append("$indent  <p><strong>Лайки:</strong> ${news.favoritesCount}, <strong>Комментарии:</strong> ${news.commentsCount}</p>\n")
        builder.append("$indent  <p><strong>Рейтинг:</strong> ${"%.2f".format(news.calculateRating())}</p>\n")
        builder.append("$indent</div>\n")
    }
}

fun html(init: HTML.() -> Unit): HTML {
    val html = HTML()
    html.init()
    return html
}

fun renderNewsPage(newsList: List<News>): String {
    return html {
        head {
            title { +"Новости" }
        }
        body {
            h1 { +"Последние новости" }
            for (news in newsList) {
                newsBlock(news) { }
            }
        }
    }.toString()
}