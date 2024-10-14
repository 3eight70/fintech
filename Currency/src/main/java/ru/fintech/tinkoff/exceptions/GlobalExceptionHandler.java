package ru.fintech.tinkoff.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import ru.fintech.tinkoff.dto.ExceptionResponse;

import java.time.Instant;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Ошибка валидации";

        int status = HttpStatus.BAD_REQUEST.value();
        log.error(e.getMessage(), e);

        return new ResponseEntity<>(new ExceptionResponse(status, errorMessage, Instant.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleValidationException(WebExchangeBindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Ошибка валидации";

        int status = HttpStatus.BAD_REQUEST.value();
        log.error(e.getMessage(), e);

        return new ResponseEntity<>(new ExceptionResponse(status, errorMessage, Instant.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ExceptionResponse> handle(ServiceUnavailableException e) {
        log.error(e.getMessage(), e);
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Сервис недоступен",
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Retry-After", "3600")
                .body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionResponse> handle(NotFoundException e) {
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handle(BadRequestException e) {
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionResponse> handle(Exception e) {
        log.error(e.getMessage(), e);
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Что-то пошло не так",
                Instant.now()
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
