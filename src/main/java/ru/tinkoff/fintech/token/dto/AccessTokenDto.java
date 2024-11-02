package ru.tinkoff.fintech.token.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для токена доступа")
public class AccessTokenDto {
    @Schema(description = "jwt токен")
    private String accessToken;
}
