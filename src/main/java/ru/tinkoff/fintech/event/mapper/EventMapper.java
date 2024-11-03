package ru.tinkoff.fintech.event.mapper;

import ru.tinkoff.fintech.event.dto.EventDto;
import ru.tinkoff.fintech.event.dto.EventRequestDto;
import ru.tinkoff.fintech.event.entity.Event;

public final class EventMapper {
    public static EventDto eventToEventDto(Event event) {
        return new EventDto(
                event.getId(),
                event.getPlaceId(),
                event.getName(),
                event.getDate()
        );
    }

    public static Event requestEventDtoToEvent(EventRequestDto dto) {
        return new Event(
                dto.getPlaceId(),
                dto.getName(),
                dto.getDate()
        );
    }
}
