package ru.tinkoff.fintech.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для создания/обновления событий")
public class EventRequestDto {
    @Schema(description = "Идентификатор места", example = "5asd3d9f-4b18-44fd-8d27-e6d487ab123d")
    @NotNull(message = "Идентификатор места должен быть указан")
    private UUID placeId;
    @Schema(description = "Название события", example = "Чудо")
    @NotNull(message = "Название должно быть указано")
    @NotBlank(message = "Название должно быть указано")
    private String name;
    @Schema(description = "Время события")
    @NotNull(message = "Время события должно быть указано")
    private LocalDateTime date;
}
