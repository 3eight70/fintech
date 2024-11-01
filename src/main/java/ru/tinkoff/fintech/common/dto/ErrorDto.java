package ru.tinkoff.fintech.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для ошибок валидации")
public class ErrorDto {
    @Schema(description = "Поле ошибки", example = "login")
    private String field;
    @Schema(description = "Сообщение ошибки", example = "Логин должен быть указан")
    private String message;
}
