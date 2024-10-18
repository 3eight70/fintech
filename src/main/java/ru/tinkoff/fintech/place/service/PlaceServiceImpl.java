package ru.tinkoff.fintech.place.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.tinkoff.fintech.common.dto.Response;
import ru.tinkoff.fintech.common.exception.BadRequestException;
import ru.tinkoff.fintech.place.dto.PlaceDetailedDto;
import ru.tinkoff.fintech.place.dto.PlaceDto;
import ru.tinkoff.fintech.place.dto.PlaceRequestDto;
import ru.tinkoff.fintech.place.exception.PlaceNotFoundException;
import ru.tinkoff.fintech.place.mapper.PlaceMapper;
import ru.tinkoff.fintech.place.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepository placeRepository;

    @Override
    public PlaceDto updatePlace(UUID placeId, PlaceRequestDto placeDto) {
        var place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));

        placeRepository.findPlaceByTitle(placeDto.getTitle())
                .filter(existingPlace -> !existingPlace.getId().equals(placeId))
                .ifPresent(existingPlace -> {
                    throw new BadRequestException("Место с таким названием уже существует");
                });

        place.setTitle(placeDto.getTitle());
        place.setSlug(placeDto.getSlug());
        place.setDescription(placeDto.getDescription());

        placeRepository.save(place);

        return PlaceMapper.placeToPlaceDto(place);
    }

    @Override
    public Response deletePlace(UUID placeId) {
        var place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));

        placeRepository.delete(place);

        return new Response(
                HttpStatus.OK.value(),
                "Место успешно удалено",
                Instant.now()
        );
    }

    @Override
    public PlaceDto createPlace(PlaceRequestDto placeDto) {
        placeRepository.findPlaceByTitle(placeDto.getTitle())
                .ifPresent(place -> {
                    throw new BadRequestException("Место с таким названием уже существует");
                });

        var place = PlaceMapper.placeRequestDtoToPlace(placeDto);

        placeRepository.save(place);

        return PlaceMapper.placeToPlaceDto(place);
    }

    @Override
    public PlaceDetailedDto getPlace(UUID placeId) {
        var place = placeRepository.findPlaceById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException(placeId));

        return PlaceMapper.placeToPlaceDetailedDto(place);
    }

    @Override
    public List<PlaceDto> getPlaces() {
        return placeRepository.findAll()
                .stream()
                .map(PlaceMapper::placeToPlaceDto)
                .toList();
    }
}
