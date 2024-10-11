package ru.fintech.tinkoff.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.fintech.tinkoff.dto.EventDto
import ru.fintech.tinkoff.service.EventMonoService
import ru.fintech.tinkoff.service.EventService
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

@RestController
class EventController(
    private val eventService: EventService,
    private val eventMonoService: EventMonoService
) {
    /**
     * @param budget сумма в валюте [currency]
     * @param currency код валюты, в которой указан [budget]
     * @param dateFrom должен быть в формате "dd.MM.yyyy"
     * @param dateTo должен быть в формате "dd.MM.yyyy"
     */
    @GetMapping("/events")
    fun getEvents(
        @RequestParam(name = "budget") budget: BigDecimal,
        @RequestParam(name = "currency") currency: String,
        @RequestParam(name = "dateFrom", required = false) dateFrom: String?,
        @RequestParam(name = "dateTo", required = false) dateTo: String?
    ): CompletableFuture<List<EventDto>> = eventService.getEvents(
        dateTo = dateTo,
        dateFrom = dateFrom,
        currency = currency,
        budget = budget
    )

    @GetMapping("/reactor")
    fun getReactorEvents(
        @RequestParam(name = "budget") budget: BigDecimal,
        @RequestParam(name = "currency") currency: String,
        @RequestParam(name = "dateFrom", required = false) dateFrom: String?,
        @RequestParam(name = "dateTo", required = false) dateTo: String?
    ): Mono<List<EventDto>> = eventMonoService.getEvents(
        dateTo = dateTo,
        dateFrom = dateFrom,
        currency = currency,
        budget = budget
    )
}