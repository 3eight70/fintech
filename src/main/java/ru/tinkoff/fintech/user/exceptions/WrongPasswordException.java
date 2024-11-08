package ru.tinkoff.fintech.user.exceptions;

import ru.tinkoff.fintech.common.exception.BadRequestException;

public class WrongPasswordException extends BadRequestException {
    public WrongPasswordException() {
        super("Указанный пароль не соответствует нынешнему");
    }
}
