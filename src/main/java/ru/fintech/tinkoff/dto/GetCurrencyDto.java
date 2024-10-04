package ru.fintech.tinkoff.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для запросов на получение валюты")
public class GetCurrencyDto {
    private String currency;
    private BigDecimal rate;
}
