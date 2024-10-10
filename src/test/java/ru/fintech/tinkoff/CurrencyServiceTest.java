package ru.fintech.tinkoff;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.fintech.tinkoff.dto.ConvertCurrencyRequestDto;
import ru.fintech.tinkoff.dto.ConvertCurrencyResponseDto;
import ru.fintech.tinkoff.exceptions.BadRequestException;
import ru.fintech.tinkoff.exceptions.NotFoundException;
import ru.fintech.tinkoff.exceptions.ServiceUnavailableException;
import ru.fintech.tinkoff.service.CurrencyService;

import java.io.IOException;
import java.math.BigDecimal;

//Валидация данных, приходящих в контроллерах проходит в сервисе
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CurrencyServiceTest {
    @Autowired
    private CurrencyService currencyService;

    private MockWebServer mockWebServer;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("currency.api.url", () -> "http://localhost:42312");
    }

    @BeforeEach
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(42312);

        mockWebServer.enqueue(createMockResponseWithStatusCode(200, TestUtils.getXmlResponse()));
        mockWebServer.enqueue(createMockResponseWithStatusCode(200, TestUtils.getXmlResponse()));
    }

    @AfterEach
    public void after() throws IOException {
        mockWebServer.close();
    }

    @Test
    @DisplayName("При получении валют и курсов на них, должен конвертировать")
    public void shouldConvertCurrency() {
        var requestDto = new ConvertCurrencyRequestDto("USD", "RUB", BigDecimal.ONE);
        var expectedResult = new ConvertCurrencyResponseDto("USD", "RUB", BigDecimal.valueOf(95.0262));

        ConvertCurrencyResponseDto result = currencyService.convert(requestDto);

        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Должен выкидывать BadRequest, когда валюты не существует")
    public void shouldThrowBadRequestExceptionWhenValuteNotFound() {
        String code = "CASCAS";

        Assertions.assertThrows(BadRequestException.class, () -> currencyService.get(code));
    }

    @Test
    @DisplayName("Должен выкидывать NotFoundException, когда валюта не найдена")
    public void shouldThrowNotFoundExceptionWhenValuteNotFound() {
        //Предварительно удалил данную валюту из дневных курсов, но она есть в общем списке валют
        String code = "AUD";

        Assertions.assertThrows(NotFoundException.class, () -> currencyService.get(code));
    }

    @Test
    @DisplayName("Должен выкидывать BadRequest, когда отсутствует необходимое поле")
    public void shouldThrowBadRequestExceptionWhenRequiredFieldIsNull() {
        var dto = new ConvertCurrencyRequestDto();

        Assertions.assertThrows(BadRequestException.class, () -> currencyService.convert(dto));
    }

    @Test
    @DisplayName("Должен выкидывать BadRequest, когда код валюты невалиден")
    public void shouldThrowBadRequestExceptionWhenCurrencyCodeIsInvalid() {
        Assertions.assertThrows(BadRequestException.class, () -> currencyService.get("INVALID"));
    }

    @Test
    @DisplayName("Должен выкидывать ServiceUnavailable, когда ЦБ недоступен")
    public void shouldThrowServiceUnavailableExceptionWhenBankIsUnavailable() throws IOException {
        mockWebServer.close();

        Assertions.assertThrows(ServiceUnavailableException.class, () -> currencyService.get("USD"));
    }

    private MockResponse createMockResponseWithStatusCode(Integer statusCode, String body) {
        return new MockResponse()
                .setResponseCode(statusCode)
                .setBody(body);
    }
}

