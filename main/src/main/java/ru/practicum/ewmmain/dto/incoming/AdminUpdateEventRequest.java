package ru.practicum.ewmmain.dto.incoming;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmmain.model.Location;

import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class AdminUpdateEventRequest {
    String title;
    String annotation;
    String description;
    Long category;
    Boolean paid;
    Integer participantLimit;
    Location location;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Boolean requestModeration;
}