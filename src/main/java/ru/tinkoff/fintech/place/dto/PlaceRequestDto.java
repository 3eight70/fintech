package ru.tinkoff.fintech.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для создания/обновления мест")
public class PlaceRequestDto {
    @Schema(description = "Название места", example = "Памятник")
    @NotNull(message = "Название должно быть указано")
    @NotBlank(message = "Название должно быть указано")
    private String title;
    @Schema(description = "Название места транслитом", example = "Pamyatnik")
    @NotNull(message = "Название транслитом должно быть указано")
    @NotBlank(message = "Название транслитом должно быть указано")
    private String slug;
    @Schema(description = "Описание места", example = "Памятник посвященный Петьке")
    @NotNull(message = "Описание должно быть указано")
    @NotBlank(message = "Описание должно быть указано")
    private String description;
}
