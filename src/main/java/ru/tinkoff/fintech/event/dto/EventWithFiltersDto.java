package ru.tinkoff.fintech.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для поиска событий по фильтрам")
public class EventWithFiltersDto {
    @Schema(description = "name", example = "Чудо")
    private String name;

    @Schema(description = "Минимальное время, в которое произойдет событие")
    private LocalDateTime timeFrom;

    @Schema(description = "Максимальное время, в которое произойдет событие")
    private LocalDateTime timeTo;

    @Schema(description = "Идентификатор места", example = "9eab3d9f-4b18-44fd-8d27-e6d487ab984e")
    private UUID placeId;
}
