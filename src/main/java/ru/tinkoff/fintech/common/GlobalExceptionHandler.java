package ru.tinkoff.fintech.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tinkoff.fintech.common.dto.ErrorDto;
import ru.tinkoff.fintech.common.dto.ErrorResponse;
import ru.tinkoff.fintech.common.dto.Response;
import ru.tinkoff.fintech.common.exception.BadRequestException;
import ru.tinkoff.fintech.common.exception.ForbiddenException;
import ru.tinkoff.fintech.common.exception.NotFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(ConstraintViolationException ex) {
        List<ErrorDto> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new ErrorDto(violation.getPropertyPath().toString(), violation.getMessage()));
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации",
                Instant.now(),
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(
                new Response(
                        HttpStatus.BAD_REQUEST.value(),
                        "Отсутствует необходимый параметр запроса " + e.getParameterName(),
                        Instant.now()
                ),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Response> handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(
                new Response(
                        HttpStatus.BAD_REQUEST.value(),
                        e.getMessage(),
                        Instant.now()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(
                new Response(
                        HttpStatus.NOT_FOUND.value(),
                        e.getMessage(),
                        Instant.now()
                ),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response> handleBadCredentialsException(BadCredentialsException e) {
        Response response = new Response(
                HttpStatus.UNAUTHORIZED.value(),
                "Неверный логин или пароль",
                Instant.now()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Response> handleForbiddenException(ForbiddenException e) {
        Response response = new Response(
                HttpStatus.FORBIDDEN.value(),
                e.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {
        log.error(e.getMessage(), e);
        Response response = new Response(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Что-то пошло не так",
                Instant.now()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
