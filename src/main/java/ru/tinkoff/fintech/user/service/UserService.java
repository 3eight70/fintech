package ru.tinkoff.fintech.user.service;

import ru.tinkoff.fintech.common.dto.Response;
import ru.tinkoff.fintech.token.dto.AccessTokenDto;
import ru.tinkoff.fintech.user.dto.ChangePasswordDto;
import ru.tinkoff.fintech.user.dto.LoginCredentials;
import ru.tinkoff.fintech.user.dto.RegisterUserDto;
import ru.tinkoff.fintech.user.dto.UserDto;

public interface UserService {
    AccessTokenDto registerUser(RegisterUserDto registerUserDto);
    AccessTokenDto loginUser(LoginCredentials loginCredentials);
    Response logoutUser(UserDto userDto);
    Response changePassword(UserDto userDto, ChangePasswordDto changePasswordDto);
}
