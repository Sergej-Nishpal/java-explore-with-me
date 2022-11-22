package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Value
@Builder
public class UpdateEventRequest {

    @Positive
    Long eventId;

    @Size(min = 20, max = 2000, message = "Длина ограничена: от 20 до 2000 символов!")
    @NotBlank
    String annotation;

    @Positive
    Long categoryId;

    @Size(min = 20, max = 7000, message = "Длина ограничена: от 20 до 7000 символов!")
    String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    Boolean paid;

    @Positive
    Integer participantLimit;

    @Size(min = 3, max = 120, message = "Длина ограничена: от 3 до 120 символов!")
    @NotBlank
    String title;
}
