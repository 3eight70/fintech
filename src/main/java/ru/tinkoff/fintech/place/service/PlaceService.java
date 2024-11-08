package ru.tinkoff.fintech.place.service;

import java.util.List;
import java.util.UUID;
import ru.tinkoff.fintech.common.dto.Response;
import ru.tinkoff.fintech.place.dto.PlaceDetailedDto;
import ru.tinkoff.fintech.place.dto.PlaceDto;
import ru.tinkoff.fintech.place.dto.PlaceRequestDto;

public interface PlaceService {
    PlaceDto updatePlace(UUID placeId, PlaceRequestDto placeDto);

    Response deletePlace(UUID placeId);

    PlaceDto createPlace(PlaceRequestDto placeDto);

    PlaceDetailedDto getPlace(UUID placeId);

    List<PlaceDto> getPlaces();
}
