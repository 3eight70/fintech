package ru.fintech.tinkoff;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import ru.fintech.tinkoff.controller.CurrencyController;
import ru.fintech.tinkoff.dto.ConvertCurrencyRequestDto;
import ru.fintech.tinkoff.service.CurrencyService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebFluxTest(CurrencyController.class)
public class ControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CurrencyService currencyService;

    private final ConvertCurrencyRequestDto invalidRequest = new ConvertCurrencyRequestDto("USD", "EUR", BigDecimal.valueOf(-10.0));

    @Test
    @DisplayName("Должен вернуть 400 при некорректном запросе на конвертацию")
    public void shouldThrowNotfoundWhenInvalidConvertRequest() throws Exception {
        webTestClient.post()
                .uri("/currencies/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Должен вернуть 400, если при запросе на конвертацию присутствуют недостающие поля")
    public void shouldThrowBadRequestWhenFieldIsNull() throws Exception {
        String jsonContent = loadJsonFromFile("src/test/resources/convert/invalidConvertRequest.json");

        webTestClient.post()
                .uri("/currencies/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonContent)
                .exchange()
                .expectStatus().isBadRequest();
    }

    private String loadJsonFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}