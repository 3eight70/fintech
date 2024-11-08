package ru.tinkoff.fintech.place.mapper;

import ru.tinkoff.fintech.event.mapper.EventMapper;
import ru.tinkoff.fintech.place.dto.PlaceDetailedDto;
import ru.tinkoff.fintech.place.dto.PlaceDto;
import ru.tinkoff.fintech.place.dto.PlaceRequestDto;
import ru.tinkoff.fintech.place.entity.Place;

public final class PlaceMapper {
    public static PlaceDto placeToPlaceDto(Place place) {
        return new PlaceDto(
                place.getId(),
                place.getTitle(),
                place.getSlug(),
                place.getDescription()
        );
    }

    public static Place placeRequestDtoToPlace(PlaceRequestDto dto) {
        return new Place(
                dto.getTitle(),
                dto.getSlug(),
                dto.getDescription()
        );
    }

    public static PlaceDetailedDto placeToPlaceDetailedDto(Place place) {
        return new PlaceDetailedDto(
                place.getId(),
                place.getTitle(),
                place.getSlug(),
                place.getDescription(),
                place.getEvents()
                        .stream()
                        .map(EventMapper::eventToEventDto)
                        .toList()
        );
    }
}
