package ru.tinkoff.fintech.user.exceptions;

import ru.tinkoff.fintech.common.exception.BadRequestException;

public class UserAlreadyExistsException extends BadRequestException {
    public UserAlreadyExistsException(String login) {
        super("Пользователь с логином: " + login + " уже существует");
    }
}
