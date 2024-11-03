package ru.tinkoff.fintech.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tinkoff.fintech.user.enums.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "dto пользователя")
public class UserDto {
    @Schema(description = "Идентификатор пользователя")
    private UUID id;
    @Schema(description = "Логин пользователя", example = "Petya1337")
    private String login;
    @Schema(description = "Роль пользователя", example = "ADMIN")
    private Role role;
}
