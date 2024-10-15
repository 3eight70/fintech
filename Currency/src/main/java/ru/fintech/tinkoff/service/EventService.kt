package ru.fintech.tinkoff.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.fintech.tinkoff.dto.ConvertCurrencyRequestDto
import ru.fintech.tinkoff.dto.EventDto
import ru.fintech.tinkoff.dto.KudaGoEvent
import ru.fintech.tinkoff.utils.EventJsonParser
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.CompletableFuture

interface EventService {
    fun getEvents(
        dateFrom: String?,
        dateTo: String?,
        currency: String,
        budget: BigDecimal
    ): CompletableFuture<List<EventDto>>
}

@Service
class EventServiceImpl @Autowired constructor(
    private val currencyService: CurrencyService,
    @Value("\${events.api.url}") private val url: String
) : EventService {
    private val client: HttpClient = HttpClient(CIO)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getEvents(
        dateFrom: String?,
        dateTo: String?,
        currency: String,
        budget: BigDecimal
    ): CompletableFuture<List<EventDto>> {
        val sinceInstant = dateFrom?.let { parseToUnixTimestamp(it, dateFormatter) }
        val untilInstant = dateTo?.let { parseToUnixTimestamp(it, dateFormatter) }

        val today = Instant.now()
        val finalStartDate: Instant = sinceInstant ?: today
        val finalEndDate: Instant = untilInstant ?: finalStartDate.plus(6, ChronoUnit.DAYS)

        val eventsFuture = CompletableFuture<List<KudaGoEvent>>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                log.info("Отправлен запрос на получение событий по датам: $finalStartDate-$finalEndDate")
                val response =
                    client.get("$url/public-api/v1.4/events?actual_since=$finalStartDate&actual_until=$finalEndDate&fields=price,title,is_free")
                        .bodyAsText()
                log.debug("Получен ответ: {}", response)
                val events = EventJsonParser.parse(response)
                eventsFuture.complete(events)
            } catch (e: Exception) {
                log.error("Во время получения событий что-то пошло не так", e)
                eventsFuture.complete(emptyList())
            }
        }

        val convertCurrencyFuture = CompletableFuture.supplyAsync {
            currencyService.convert(
                ConvertCurrencyRequestDto(
                    currency,
                    "RUB",
                    budget
                )
            )
        }
            .handle { result, throwable ->
                if (throwable != null) {
                    log.error("Ошибка при конвертации валюты: {}", throwable.message)
                    null
                } else {
                    result
                }
            }

        val resultFuture = CompletableFuture<List<EventDto>>()

        eventsFuture.thenAcceptBoth(convertCurrencyFuture) { events, convertedBudget ->
            val suitableEvents = events.filter { event ->
                val parsedPrice = parsePrice(event.price)
                event.isFree ||
                        (parsedPrice?.let { it <= convertedBudget?.convertedAmount } ?: false)
            }

            val eventDtos = suitableEvents.map { event ->
                EventDto(
                    name = event.title,
                    price = event.price
                )
            }

            log.debug(
                "Возможные для посещения события: {}",
                eventDtos.joinToString(", ") { "${it.name} (Цена: ${it.price})" })
            resultFuture.complete(eventDtos)
        }

        return resultFuture
    }

    companion object {
        fun parseToUnixTimestamp(dateString: String, dateTimeFormatter: DateTimeFormatter) =
            try {
                val localDate = LocalDate.parse(dateString, dateTimeFormatter)
                localDate.atStartOfDay(ZoneOffset.UTC).toInstant()
            } catch (e: Exception) {
                null
            }

        fun parsePrice(input: String): BigDecimal? {
            val inputWithoutSpaces = input.replace(" ", "")
            val regex = "\\d+".toRegex()
            val matchResult = regex.find(inputWithoutSpaces)
            return matchResult?.value?.toBigDecimal()
        }
    }
}