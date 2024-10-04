package ru.fintech.tinkoff.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для запросов на конвертацию валюты")
public class ConvertCurrencyRequestDto {
    @Schema(description = "Изначальная валюта")
    @NotNull(message = "Изначальная валюта должна быть заполнена")
    private String fromCurrency;
    @Schema(description = "Валюта, в которую конвертируем")
    @NotNull(message = "Конвертируемая валюта должна быть заполнена")
    private String toCurrency;
    @Schema(description = "Сумма в изначальной валюте")
    @NotNull(message = "Сумма должна быть заполнена")
    private BigDecimal amount;
}
