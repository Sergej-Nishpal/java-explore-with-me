package ru.practicum.ewmmain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import ru.practicum.ewmmain.model.EventState;
import ru.practicum.ewmmain.model.Location;

import java.time.LocalDateTime;

@Value
@Builder
public class EventFullDto {
    Long id;
    String title;
    String annotation;
    String description;
    CategoryDto category;
    Boolean paid;
    Integer participantLimit;
    Location location;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    UserShortDto initiator;
    Boolean requestModeration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    EventState state;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    Long confirmedRequests;
    Long views;
}
