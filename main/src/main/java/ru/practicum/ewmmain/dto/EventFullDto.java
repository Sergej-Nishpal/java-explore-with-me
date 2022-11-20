package ru.practicum.ewmmain.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewmmain.model.EventState;
import ru.practicum.ewmmain.model.Location;

import java.time.LocalDateTime;

@Value
@Builder
public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    LocalDateTime createdOn;
    String description;
    LocalDateTime eventDate;
    UserShortDto initiator;
    Location location;
    Boolean paid;
    Integer participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    Long views;
}
