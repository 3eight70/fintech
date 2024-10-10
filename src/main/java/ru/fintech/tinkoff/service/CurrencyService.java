package ru.fintech.tinkoff.service;

import ru.fintech.tinkoff.dto.ConvertCurrencyRequestDto;
import ru.fintech.tinkoff.dto.ConvertCurrencyResponseDto;
import ru.fintech.tinkoff.dto.GetCurrencyDto;

public interface CurrencyService {
    ConvertCurrencyResponseDto convert(ConvertCurrencyRequestDto request);
    GetCurrencyDto get(String code);
}
