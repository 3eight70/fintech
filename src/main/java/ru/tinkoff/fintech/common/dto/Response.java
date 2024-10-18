package ru.tinkoff.fintech.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ на запросы")
public class Response {
    @Schema(description = "Статус ответа", example = "200")
    private int status;
    @Schema(description = "Сообщение ответа", example = "Место успешно удалено")
    private String message;
    @Schema(description = "Время ответа")
    private Instant timestamp;
}
