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
    String title;
    String annotation;
    String description;
    CategoryDto category;
    Boolean paid;
    Integer participantLimit;
    Location location;
    LocalDateTime eventDate;
    UserShortDto initiator;
    Boolean requestModeration;
    LocalDateTime createdOn;
    EventState state;
    LocalDateTime publishedOn;
    Long confirmedRequests;
    Long views;
}
