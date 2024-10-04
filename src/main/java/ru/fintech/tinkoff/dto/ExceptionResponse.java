package ru.fintech.tinkoff.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для возвращения сообщений с исключениями")
public class ExceptionResponse {
    @Schema(description = "Статус ответа")
    private int code;
    @Schema(description = "Сообщение")
    private String message;
    @Schema(description = "Время возникновения")
    private Instant timestamp;
}
