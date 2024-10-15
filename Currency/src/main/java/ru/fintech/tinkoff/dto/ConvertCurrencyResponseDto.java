package ru.fintech.tinkoff.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для ответов на запросы на преобразование валюты")
public class ConvertCurrencyResponseDto {
    @Schema(description = "Изначальная валюта", example = "RUB")
    private String fromCurrency;
    @Schema(description = "Валюта, в которую конвертируем", example = "USD")
    private String toCurrency;
    @Schema(description = "Сумма в конвертированной валюте", example = "1")
    private BigDecimal convertedAmount;

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }
}
