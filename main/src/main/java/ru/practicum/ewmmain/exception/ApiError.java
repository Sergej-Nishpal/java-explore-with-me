package ru.practicum.ewmmain.exception;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class ApiError {
    StackTraceElement[] errors;
    String message;
    String reason;
    String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
}