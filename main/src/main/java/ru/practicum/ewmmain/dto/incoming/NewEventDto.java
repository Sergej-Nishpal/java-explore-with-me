package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewmmain.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class NewEventDto {

    @Size(min = 20, max = 2000, message = "Длина ограничена: от 20 до 2000 символов!")
    @Length
    @NotBlank
    String annotation;

    @Positive
    Long categoryId;

    @Size(min = 20, max = 2000, message = "Длина ограничена: от 20 до 7000 символов!")
    String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    Location location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Длина ограничена: от 3 до 120 символов!")
    @NotBlank
    String title;
}
