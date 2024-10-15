package ru.fintech.tinkoff.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.fintech.tinkoff.dto.ConvertCurrencyRequestDto;
import ru.fintech.tinkoff.dto.ConvertCurrencyResponseDto;
import ru.fintech.tinkoff.dto.GetCurrencyDto;
import ru.fintech.tinkoff.dto.xml.CurrencyItem;
import ru.fintech.tinkoff.dto.xml.CurrencyList;
import ru.fintech.tinkoff.dto.xml.ValCurs;
import ru.fintech.tinkoff.dto.xml.Valute;
import ru.fintech.tinkoff.exceptions.BadRequestException;
import ru.fintech.tinkoff.exceptions.NotFoundException;
import ru.fintech.tinkoff.exceptions.ServiceUnavailableException;

@Service
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {
    private final String apiUrl;

    private final RestClient restClient;
    private final Set<String> charCodeSet;
    private final XmlMapper mapper = new XmlMapper();

    @Autowired
    public CurrencyServiceImpl(
            RestClient restClient,
            @Value("${currency.api.url}") String apiUrl,
            @Value("${currency.api.valute.url}") String valuteUrl
    ) {
        this.restClient = restClient;
        this.apiUrl = apiUrl;
        this.charCodeSet = getValuteCodes(valuteUrl);
    }

    @Override
    @CircuitBreaker(name = "currencyService", fallbackMethod = "fallbackConvertCurrency")
    public ConvertCurrencyResponseDto convert(ConvertCurrencyRequestDto request) {
        log.debug("Получен запрос на конвертацию валюты: {}", request);
        if (request.getFromCurrency() == null){
            throw new BadRequestException("Конвертируемая валюта должна быть указана");
        }

        if (request.getToCurrency() == null){
            throw new BadRequestException("Валюта, в которую конвертируем должна быть указана");
        }

        if (request.getAmount() == null){
            throw new BadRequestException("Сумма конвертируемой валюты должна быть указана");
        }

        String fromCurrencyCode = request.getFromCurrency();
        String toCurrencyCode = request.getToCurrency();

//        if (fromCurrencyCode.equals(toCurrencyCode)) {
//            throw new BadRequestException("Конвертация одной и той же валюты отключена");
//        }

        GetCurrencyDto fromCurrency = get(fromCurrencyCode);
        GetCurrencyDto toCurrency = get(toCurrencyCode);
        BigDecimal convertedAmount = request.getAmount()
                .multiply(fromCurrency.getRate())
                .divide(toCurrency.getRate(), 4, RoundingMode.HALF_UP);

        ConvertCurrencyResponseDto response = new ConvertCurrencyResponseDto();
        response.setFromCurrency(request.getFromCurrency());
        response.setToCurrency(request.getToCurrency());
        response.setConvertedAmount(convertedAmount);

        log.debug("Конвертация завершена: {} {} = {} {}",
                request.getAmount(),
                request.getFromCurrency(),
                convertedAmount,
                request.getToCurrency());

        return response;
    }

    public void fallbackConvertCurrency(ConvertCurrencyRequestDto dto, Throwable throwable) {
        log.error("Сработал fallback на метод convert", throwable);
        throw new ServiceUnavailableException("В данный момент сервис не доступен, попробуйте позже");
    }

    @Override
    @CircuitBreaker(name = "currencyService", fallbackMethod = "fallbackGetCurrency")
    @Cacheable(value = "currencyRates", key = "#code", unless = "#result == null")
    public GetCurrencyDto get(String code) {
        log.info("Получен запрос на получение валюты с кодом: {}", code);
        if (!charCodeSet.contains(code) || isInvalidCurrency(code)) {
            throw new BadRequestException("Валюты с указанным кодом не существует:" + code);
        }

        String xmlResponse = fetchCurrencyData(apiUrl);

        return getCurrencyRate(code, xmlResponse);
    }

    public void fallbackGetCurrency(String code, Throwable throwable) {
        log.error("Сработал fallback на метод get", throwable);
        throw new ServiceUnavailableException("В данный момент сервис не доступен, попробуйте позже");
    }

    private String fetchCurrencyData(String url) {
        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Сервис ЦБ недоступен");
        }
    }

    private ValCurs getCurrency(String xmlResponse) {
        try {
            return mapper.readValue(xmlResponse, ValCurs.class);
        } catch (Exception e) {
            log.error("Ошибка парсинга XML ответа", e);
            throw new RuntimeException("При парсинге данных что-то пошло не так");
        }
    }

    private GetCurrencyDto getCurrencyRate(String code, String xmlResponse) {
        if (code.equalsIgnoreCase("RUB")) {
            return new GetCurrencyDto("RUB", BigDecimal.ONE);
        }

        ValCurs valCurs = getCurrency(xmlResponse);
        if (valCurs != null) {
            for (Valute valute : valCurs.getValutes()) {
                if (valute.getCharCode().equalsIgnoreCase(code)) {
                    return new GetCurrencyDto(
                            valute.getCharCode(),
                            BigDecimal.valueOf(Double.parseDouble(valute.getValue().replace(",", ".")))
                    );
                }
            }
        }
        throw new NotFoundException("Валюта не найдена");
    }

    private boolean isInvalidCurrency(String code) {
        try {
            Currency.getInstance(code);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private Set<String> getValuteCodes(String url) {
        String xmlResponse = fetchCurrencyData(url);

        CurrencyList list = getCurrencyItems(xmlResponse);

        Set<String> set = new HashSet<>();

        for (CurrencyItem item : list.getItems()) {
            set.add(item.getIsoCharCode());
        }
        set.add("RUB");

        return set;
    }

    private CurrencyList getCurrencyItems(String xmlResponse) {
        try {
            return mapper.readValue(xmlResponse, CurrencyList.class);
        } catch (Exception e) {
            log.error("Ошибка парсинга XML ответа", e);
            throw new RuntimeException("При парсинге данных что-то пошло не так");
        }
    }
}
