package ru.practicum.ewmmain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class) //TODO
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle400Error(final RuntimeException e) {
        log.error("400 - Ошибка валидации: {} ", e.getMessage(), e);
        return ApiError.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason("The request was malformed.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler() //TODO
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handle403Error(final RuntimeException e) {
        log.error("403 - Действие запрещено: {} ", e.getMessage(), e);
        return ApiError.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .status(HttpStatus.FORBIDDEN.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler() //TODO
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle404Error(final RuntimeException e) {
        log.error("404 - Объект не найден: {} ", e.getMessage(), e);
        return ApiError.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle409Error(final RuntimeException e) {
        log.error("409 - Конфликт данных: {} ", e.getMessage(), e);
        return ApiError.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason("Integrity constraint has been violated.")
                .status(HttpStatus.CONFLICT.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle500Error(final RuntimeException e) {
        log.error("500 - Внутренняя ошибка сервера: {} ", e.getMessage(), e);
        return ApiError.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason("Error occurred.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .timestamp(LocalDateTime.now())
                .build();
    }
}