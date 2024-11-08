package ru.tinkoff.fintech.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto для регистрации пользователя")
public class RegisterUserDto {
    @Schema(description = "Логин пользователя", example = "Petya1338")
    @NotNull(message = "Логин должен быть указан")
    @NotBlank(message = "Логин должен быть указан")
    @Size(min = 1, message = "Длина логина должна быть больше 0")
    private String login;
    @Schema(description = "Пароль пользователя", example = "qwertyuiop")
    @NotNull(message = "Пароль должен быть указан")
    @NotBlank(message = "Пароль должен быть указан")
    @Size(min = 1, message = "Длина пароля должна быть больше 0")
    private String password;
}
