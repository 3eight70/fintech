package ru.tinkoff.fintech.event.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.fintech.common.dto.Response;
import ru.tinkoff.fintech.event.dto.EventDto;
import ru.tinkoff.fintech.event.dto.EventRequestDto;
import ru.tinkoff.fintech.event.dto.EventWithFiltersDto;
import ru.tinkoff.fintech.event.service.EventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
@Tag(name = "События", description = "Контроллер, отвечающий за работу с событиями")
public class EventController {
    private final EventService eventService;

    @Operation(
            summary = "Обновление события",
            description = "Позволяет обновить информацию по событию"
    )
    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(
            @Parameter(description = "Идентификатор события") @PathVariable UUID eventId,
            @Validated @RequestBody EventRequestDto eventDto
    ) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, eventDto));
    }

    @Operation(
            summary = "Удаление события",
            description = "Позволяет удалить событие"
    )
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Response> deleteEvent(
            @Parameter(description = "Идентификатор события") @PathVariable UUID eventId
    ) {
        return ResponseEntity.ok(eventService.deleteEvent(eventId));
    }

    @Operation(
            summary = "Создание события",
            description = "Позволяет создать событие"
    )
    @PostMapping
    public ResponseEntity<EventDto> createEvent(@Validated @RequestBody EventRequestDto eventDto) {
        return ResponseEntity.ok(eventService.createEvent(eventDto));
    }

    @Operation(
            summary = "Получение события по id",
            description = "Позволяет получить информацию по конкретному событию"
    )
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(
            @Parameter(description = "Идентификатор события") @PathVariable UUID eventId
    ) {
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }

    @Operation(
            summary = "Получение событий по id места",
            description = "Позволяет получить все события, происходящие в месте"
    )
    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<EventDto>> getEventsByPlace(
            @Parameter(description = "Идентификатор события") @PathVariable UUID placeId
    ) {
        return ResponseEntity.ok(eventService.getEventsByPlace(placeId));
    }

    @Operation(
            summary = "Получение событий по фильтрам",
            description = "Позволяет получить события по определенным фильтрам"
    )
    @PostMapping("/filters")
    public ResponseEntity<List<EventDto>> getEventsWithFilters(
            @RequestBody EventWithFiltersDto eventWithFiltersDto
    ) {
        return ResponseEntity.ok(eventService.getEventsWithFilters(eventWithFiltersDto));
    }
}
