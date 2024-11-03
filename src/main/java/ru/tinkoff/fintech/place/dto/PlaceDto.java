package ru.tinkoff.fintech.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для мест")
public class PlaceDto {
    @Schema(description = "Идентификатор", example = "9eab3d9f-4b18-44fd-8d27-e6d487ab984e")
    private UUID id;
    @Schema(description = "Название места", example = "Памятник")
    private String title;
    @Schema(description = "Название места транслитом", example = "Pamyatnik")
    private String slug;
    @Schema(description = "Описание места", example = "Памятник посвященный Петьке")
    private String description;
}
