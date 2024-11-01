package ru.tinkoff.fintech.user.mapper;

import java.util.UUID;
import ru.tinkoff.fintech.user.dto.RegisterUserDto;
import ru.tinkoff.fintech.user.entity.User;
import ru.tinkoff.fintech.user.enums.Role;

public final class UserMapper {
    public static User registerDtoToEntity(RegisterUserDto registerUserDto) {
        return new User(
                UUID.randomUUID(),
                registerUserDto.getLogin(),
                registerUserDto.getPassword(),
                Role.USER
        );
    }
}
