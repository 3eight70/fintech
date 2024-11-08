package ru.tinkoff.fintech.user.exceptions;

import ru.tinkoff.fintech.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String login) {
        super("Пользователь с логином: " + login + " не найден");
    }
}
