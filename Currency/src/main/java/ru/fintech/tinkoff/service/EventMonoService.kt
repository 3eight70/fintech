package ru.fintech.tinkoff.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.fintech.tinkoff.dto.ConvertCurrencyRequestDto
import ru.fintech.tinkoff.dto.EventDto
import ru.fintech.tinkoff.service.EventServiceImpl.Companion.parsePrice
import ru.fintech.tinkoff.service.EventServiceImpl.Companion.parseToUnixTimestamp
import ru.fintech.tinkoff.utils.EventJsonParser
import java.math.BigDecimal
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

interface EventMonoService {
    fun getEvents(
        dateFrom: String?,
        dateTo: String?,
        currency: String,
        budget: BigDecimal
    ): Mono<List<EventDto>>
}

@Service
class EventMonoServiceImpl @Autowired constructor(
    private val currencyService: CurrencyService,
    @Value("\${events.api.url}") private val url: String
) : EventMonoService {
    private val client: HttpClient = HttpClient(CIO)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val log = LoggerFactory.getLogger(this::class.java)
    override fun getEvents(
        dateFrom: String?,
        dateTo: String?,
        currency: String,
        budget: BigDecimal
    ): Mono<List<EventDto>> {
        val sinceInstant = dateFrom?.let { parseToUnixTimestamp(it, dateFormatter) }
        val untilInstant = dateTo?.let { parseToUnixTimestamp(it, dateFormatter) }

        val today = Instant.now()
        val finalStartDate: Instant = sinceInstant ?: today
        val finalEndDate: Instant = untilInstant ?: finalStartDate.plus(6, ChronoUnit.DAYS)

        val eventsMono = mono {
            try {
                log.info("Отправлен запрос на получение событий по датам: $finalStartDate-$finalEndDate")
                val response =
                    client.get("$url/public-api/v1.4/events?actual_since=$finalStartDate&actual_until=$finalEndDate&fields=price,title,is_free")
                        .bodyAsText()
                log.debug("Получен ответ: {}", response)
                EventJsonParser.parse(response)
            } catch (e: Exception) {
                log.error("Во время получения событий что-то пошло не так", e)
                emptyList()
            }
        }

        val currencyMono = Mono.fromCallable {
            currencyService.convert(
                ConvertCurrencyRequestDto(
                    currency,
                    "RUB",
                    budget
                )
            )
        }

        return Mono.zip(eventsMono, currencyMono)
            .flatMap { tuple ->
                val events = tuple.t1
                val convertedBudget = tuple.t2
                val suitableEvents = events.filter { event ->
                    event.isFree || (parsePrice(event.price)?.let { it <= convertedBudget.convertedAmount } ?: false)
                }

                val eventDtos = suitableEvents.map { event ->
                    EventDto(name = event.title, price = event.price)
                }

                Mono.just(eventDtos)
            }
    }

}