package ru.fintech.tinkoff.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
    @Schema(description = "Изначальная валюта", example = "RUB")
    @NotNull(message = "Изначальная валюта должна быть заполнена")
    @NotBlank(message = "Изначальная валюта должна быть заполнена")
    private String fromCurrency;
    @Schema(description = "Валюта, в которую конвертируем", example = "USD")
    @NotNull(message = "Конвертируемая валюта должна быть заполнена")
    @NotBlank(message = "Конвертируемая валюта должна быть заполнена")
    private String toCurrency;
    @Schema(description = "Сумма в изначальной валюте", example = "100")
    @NotNull(message = "Сумма должна быть заполнена")
    @DecimalMin(value = "0", message = "Сумма не должна быть меньше или равна 0")
    private BigDecimal amount;
}
