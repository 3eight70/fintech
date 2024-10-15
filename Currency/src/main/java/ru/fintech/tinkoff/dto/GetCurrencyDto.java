package ru.fintech.tinkoff.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для запросов на получение валюты")
public class GetCurrencyDto {
    private String currency;
    private BigDecimal rate;
}
