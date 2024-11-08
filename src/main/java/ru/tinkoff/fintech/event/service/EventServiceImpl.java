package ru.tinkoff.fintech.event.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.tinkoff.fintech.common.dto.Response;
import ru.tinkoff.fintech.common.exception.BadRequestException;
import ru.tinkoff.fintech.event.dto.EventDto;
import ru.tinkoff.fintech.event.dto.EventRequestDto;
import ru.tinkoff.fintech.event.dto.EventWithFiltersDto;
import ru.tinkoff.fintech.event.entity.Event;
import ru.tinkoff.fintech.event.exception.EventNotFoundException;
import ru.tinkoff.fintech.event.mapper.EventMapper;
import ru.tinkoff.fintech.event.repository.EventRepository;
import ru.tinkoff.fintech.event.specification.EventSpecification;
import ru.tinkoff.fintech.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;

    @Override
    public EventDto updateEvent(UUID eventId, EventRequestDto eventDto) {
        var placeId = eventDto.getPlaceId();
        placeRepository.findById(placeId)
                .orElseThrow(() -> new BadRequestException("Места с id: " + placeId + "не существует"));

        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        event.setPlaceId(eventDto.getPlaceId());
        event.setDate(eventDto.getDate());
        event.setName(eventDto.getName());

        eventRepository.save(event);

        return EventMapper.eventToEventDto(event);
    }

    @Override
    public Response deleteEvent(UUID eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        eventRepository.delete(event);

        return new Response(
                HttpStatus.OK.value(),
                "Событие успешно удалено",
                Instant.now()
        );
    }

    @Override
    public EventDto createEvent(EventRequestDto eventDto) {
        var placeId = eventDto.getPlaceId();
        placeRepository.findById(placeId)
                .orElseThrow(() -> new BadRequestException("Места с id: " + placeId + "не существует"));

        var event = EventMapper.requestEventDtoToEvent(eventDto);

        eventRepository.save(event);

        return EventMapper.eventToEventDto(event);
    }

    @Override
    public EventDto getEvent(UUID eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        return EventMapper.eventToEventDto(event);
    }

    @Override
    public List<EventDto> getEventsByPlace(UUID placeId) {
        return eventRepository.findEventsByPlaceId(placeId)
                .stream()
                .map(EventMapper::eventToEventDto)
                .toList();
    }

    @Override
    public List<EventDto> getEventsWithFilters(EventWithFiltersDto eventWithFiltersDto) {
        Specification<Event> specification = Specification
                .where(EventSpecification.nameLike(eventWithFiltersDto.getName()))
                .and(EventSpecification.themeIdEquals(eventWithFiltersDto.getPlaceId()))
                .and(EventSpecification.timeGreaterOrEqualThan(eventWithFiltersDto.getTimeFrom()))
                .and(EventSpecification.timeLessOrEqualThan(eventWithFiltersDto.getTimeTo()));


        return eventRepository.findAll(specification).stream()
                .map(EventMapper::eventToEventDto)
                .toList();
    }
}
