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
@Schema(description = "dto для событий")
public class EventDto {
    @Schema(description = "Идентификатор события", example = "9eab3d9f-4b18-44fd-8d27-e6d487ab984e")
    private UUID id;
    @Schema(description = "Идентификатор места", example = "5asd3d9f-4b18-44fd-8d27-e6d487ab123d")
    private UUID placeId;
    @Schema(description = "Название события", example = "Чудо")
    private String name;
    @Schema(description = "Время события")
    private LocalDateTime date;
}
