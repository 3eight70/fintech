package ru.tinkoff.fintech.event.service;

import java.util.List;
import java.util.UUID;
import ru.tinkoff.fintech.common.dto.Response;
import ru.tinkoff.fintech.event.dto.EventDto;
import ru.tinkoff.fintech.event.dto.EventRequestDto;
import ru.tinkoff.fintech.event.dto.EventWithFiltersDto;

public interface EventService {
    EventDto updateEvent(UUID eventId, EventRequestDto eventDto);

    Response deleteEvent(UUID eventId);

    EventDto createEvent(EventRequestDto eventDto);

    EventDto getEvent(UUID eventId);

    List<EventDto> getEventsByPlace(UUID placeId);

    List<EventDto> getEventsWithFilters(EventWithFiltersDto eventWithFiltersDto);
}
