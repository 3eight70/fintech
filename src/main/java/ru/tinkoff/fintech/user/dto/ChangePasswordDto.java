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
@Schema(description = "dto для смены пароля")
public class ChangePasswordDto {
    @Schema(description = "Текущий пароль пользователя", example = "qwertyuiop")
    @NotNull(message = "Пароль должен быть указан")
    @NotBlank(message = "Пароль должен быть указан")
    @Size(min = 1, message = "Длина пароля должна быть больше 0")
    private String currentPassword;
    @Schema(description = "Новый пароль пользователя", example = "qwertyuiop123")
    @NotNull(message = "Пароль должен быть указан")
    @NotBlank(message = "Пароль должен быть указан")
    @Size(min = 1, message = "Длина пароля должна быть больше 0")
    private String newPassword;
}
