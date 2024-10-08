package ru.fintech.tinkoff.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fintech.tinkoff.dto.ConvertCurrencyRequestDto;
import ru.fintech.tinkoff.dto.ConvertCurrencyResponseDto;
import ru.fintech.tinkoff.dto.GetCurrencyDto;
import ru.fintech.tinkoff.service.CurrencyService;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
@Tag(name = "Валюта", description = "Позволяет выполнять действия, связанные с валютой")
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/rates/{code}")
    @Operation(
            summary = "Получение текущей ставки валюты",
            description = "Позволяет по коду получить ставку валюты"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ставка валюты успешно получена"),
            @ApiResponse(responseCode = "400", description = "Некорректный код валюты"),
            @ApiResponse(responseCode = "404", description = "Валюта не найдена"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервиса"),
            @ApiResponse(responseCode = "503", description = "ЦБ недоступен")
    })
    public ResponseEntity<GetCurrencyDto> getCurrency(
            @PathVariable("code") @Parameter(description = "Код валюты") String code
    ) {
        return ResponseEntity.ok(currencyService.get(code));
    }

    @PostMapping("/convert")
    @Operation(
            summary = "Конвертация валюты",
            description = "Позволяет сконвертировать одну валюту в другую"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Конвертация успешно выполнена"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос, например, сумма меньше или равна 0"),
            @ApiResponse(responseCode = "404", description = "Валюта не найдена"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервиса"),
            @ApiResponse(responseCode = "503", description = "ЦБ недоступен")
    })
    public ResponseEntity<ConvertCurrencyResponseDto> convertCurrency(
            @Valid @RequestBody ConvertCurrencyRequestDto request
    ) {
        return ResponseEntity.ok(currencyService.convert(request));
    }
}
