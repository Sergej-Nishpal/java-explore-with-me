package ru.practicum.ewmmain.dto.incoming;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmmain.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class NewEventDto {

    @Size(min = 20, max = 2000, message = "Длина ограничена: от 20 до 2000 символов!")
    @NotBlank
    String annotation;

    @Positive
    Long category;

    @Size(min = 20, max = 2000, message = "Длина ограничена: от 20 до 7000 символов!")
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @NotNull
    Location location;

    @NotNull
    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    @NotNull
    Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Длина ограничена: от 3 до 120 символов!")
    @NotBlank
    String title;
}
