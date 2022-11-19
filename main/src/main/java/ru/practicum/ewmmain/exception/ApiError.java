package ru.practicum.ewmmain.exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private Collection<Exception> errors;
    private String message;
    private String reason;
    private String status;
    private LocalDateTime timestamp;
}
