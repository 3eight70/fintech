package ru.tinkoff.fintech.token.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "dto для токена доступа")
public class AccessTokenDto {
    @Schema(description = "jwt токен")
    private String accessToken;
}
