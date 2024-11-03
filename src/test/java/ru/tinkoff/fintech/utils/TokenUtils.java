package ru.tinkoff.fintech.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TokenUtils {
    public static String extractTokenFromResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("accessToken").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при извлечении токена из ответа", e);
        }
    }
}
