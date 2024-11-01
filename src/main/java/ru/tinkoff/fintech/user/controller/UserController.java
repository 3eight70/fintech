package ru.tinkoff.fintech.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.fintech.common.dto.Response;
import ru.tinkoff.fintech.token.dto.AccessTokenDto;
import ru.tinkoff.fintech.user.dto.ChangePasswordDto;
import ru.tinkoff.fintech.user.dto.LoginCredentials;
import ru.tinkoff.fintech.user.dto.RegisterUserDto;
import ru.tinkoff.fintech.user.dto.UserDto;
import ru.tinkoff.fintech.user.exceptions.SamePasswordException;
import ru.tinkoff.fintech.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "Пользователь", description = "Отвечает за авторизацию и работу с пользователями")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(
            summary = "Авторизация",
            description = "Позволяет пользователю получить доступ в систему"
    )
    public ResponseEntity<AccessTokenDto> loginUser(
            @RequestBody @Validated LoginCredentials loginCredentials
    ) {
        loginCredentials.setPassword(passwordEncoder.encode(loginCredentials.getPassword()));

        return ResponseEntity.ok(userService.loginUser(loginCredentials));
    }

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация",
            description = "Позволяет пользователю зарегистрироваться в системе"
    )
    public ResponseEntity<AccessTokenDto> registerUser(
            @RequestBody @Validated RegisterUserDto registerUserDto
    ) {
        registerUserDto.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));

        return ResponseEntity.ok(userService.registerUser(registerUserDto));
    }

    @Operation(
            summary = "Выйти из аккаунта",
            description = "Позволяет пользователю завершить все активные сессии",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/logout")
    public ResponseEntity<Response> logoutUser(
            @AuthenticationPrincipal UserDto userDto
    ) {
        return ResponseEntity.ok(userService.logoutUser(userDto));
    }

    @Operation(
            summary = "Смена пароля",
            description = "Позволяет пользователю заменить текущий пароль на новый",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/password")
    public ResponseEntity<Response> changePassword(
            @AuthenticationPrincipal UserDto userDto,
            @RequestBody @Validated ChangePasswordDto changePasswordDto
    ) {
        if (changePasswordDto.getCurrentPassword().equals(changePasswordDto.getNewPassword())){
            throw new SamePasswordException();
        }

        changePasswordDto.setCurrentPassword(passwordEncoder.encode(changePasswordDto.getCurrentPassword()));
        changePasswordDto.setNewPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));

        return ResponseEntity.ok(userService.changePassword(userDto, changePasswordDto));
    }
}
