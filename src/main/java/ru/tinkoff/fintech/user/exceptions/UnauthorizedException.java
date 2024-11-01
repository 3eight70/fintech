package ru.tinkoff.fintech.user.exceptions;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException() {
        super("Неверный логин или пароль");
    }
}
