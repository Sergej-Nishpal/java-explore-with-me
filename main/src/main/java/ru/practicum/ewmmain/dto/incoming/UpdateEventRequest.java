package ru.practicum.ewmmain.dto.incoming;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class UpdateEventRequest {

    @Positive
    Long eventId;

    @Size(min = 3, max = 120, message = "Длина ограничена: от 3 до 120 символов!")
    @NotBlank
    String title;

    @Size(min = 20, max = 2000, message = "Длина ограничена: от 20 до 2000 символов!")
    @NotBlank
    String annotation;

    @Size(min = 20, max = 7000, message = "Длина ограничена: от 20 до 7000 символов!")
    String description;

    @Positive
    Long categoryId;

    Boolean paid;

    @Positive
    Integer participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
}