package ru.tinkoff.fintech.user.exceptions;

import ru.tinkoff.fintech.common.exception.BadRequestException;

public class SamePasswordException extends BadRequestException {
    public SamePasswordException() {
        super("Пароли совпадают");
    }
}
