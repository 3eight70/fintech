package ru.tinkoff.fintech.place.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import ru.tinkoff.fintech.place.dto.PlaceDetailedDto;
import ru.tinkoff.fintech.place.dto.PlaceDto;
import ru.tinkoff.fintech.place.dto.PlaceRequestDto;
import ru.tinkoff.fintech.place.service.PlaceService;
import ru.tinkoff.fintech.user.dto.UserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
@Tag(name = "Места", description = "Контроллер, отвечающий за работу с местами")
public class PlaceController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final PlaceService placeService;

    @Operation(
            summary = "Обновление места",
            description = "Позволяет обновить место",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{placeId}")
    public ResponseEntity<PlaceDto> updatePlace(
            @AuthenticationPrincipal UserDto userDto,
            @Parameter(description = "Идентификатор места") @PathVariable UUID placeId,
            @Validated @RequestBody PlaceRequestDto placeDto
    ) {
        log.info("Пользователь {} пытается обновить место с id: {}", userDto.getLogin(), placeId);
        PlaceDto updatedPlace = placeService.updatePlace(placeId, placeDto);
        return ResponseEntity.ok(updatedPlace);
    }

    @Operation(
            summary = "Удаление места",
            description = "Позволяет удалить место",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Response> deletePlace(
            @AuthenticationPrincipal UserDto userDto,
            @Parameter(description = "Идентификатор места") @PathVariable UUID placeId
    ) {
        log.info("Пользователь {} пытается удалить место с id: {}", userDto.getLogin(), placeId);
        return ResponseEntity.ok(placeService.deletePlace(placeId));
    }

    @Operation(
            summary = "Создание места",
            description = "Позволяет создать место",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<PlaceDto> createPlace(
            @AuthenticationPrincipal UserDto userDto,
            @Validated @RequestBody PlaceRequestDto placeDto
    ) {
        log.info("Пользователь {} пытается создать место", userDto.getLogin());
        return ResponseEntity.ok(placeService.createPlace(placeDto));
    }

    @Operation(
            summary = "Получение места",
            description = "Позволяет получить детальную информацию по месту"
    )
    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceDetailedDto> getPlace(
            @Parameter(description = "Идентификатор места") @PathVariable UUID placeId
    ) {
        return ResponseEntity.ok(placeService.getPlace(placeId));
    }

    @Operation(
            summary = "Получение списка мест",
            description = "Позволяет получить список мест"
    )
    @GetMapping
    public ResponseEntity<List<PlaceDto>> getPlaces() {
        return ResponseEntity.ok(placeService.getPlaces());
    }
}
