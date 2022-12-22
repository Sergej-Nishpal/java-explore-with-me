package ru.practicum.ewmmain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ConstraintViolationException.class, NullPointerException.class,
            HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationError(final RuntimeException e) {
        log.error("400 - Ошибка валидации: {} ", e.getMessage(), e);
        return ApiError.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason("The request was malformed.")
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({UserNotFoundException.class, CategoryNotFoundException.class,
            EventNotFoundException.class, CompilationNotFoundException.class,
            LocationNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundError(final RuntimeException e) {
        log.error("404 - Объект не найден: {} ", e.getMessage(), e);
        return ApiError.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({EventStateException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenError(final RuntimeException e) {
        log.error("403 - Запрещено: {} ", e.getMessage(), e);
        return ApiError.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason("Only published events can be requested.")
                .status(HttpStatus.FORBIDDEN.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictError(final RuntimeException e) {
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
    public ApiError handleServerError(final RuntimeException e) {
        log.error("500 - Внутренняя ошибка сервера: {} ", e.getMessage(), e);
        return ApiError.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason("Error occurred.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({WebClientRequestException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiError handleServiceError(final RuntimeException e) {
        log.error("503 - Требуемый сервис недоступен: {} ", e.getMessage(), e);
        return ApiError.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason("One of services unavailable.")
                .status(HttpStatus.SERVICE_UNAVAILABLE.name())
                .timestamp(LocalDateTime.now())
                .build();
    }
}