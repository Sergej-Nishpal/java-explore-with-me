package ru.practicum.ewmmain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
public class EventShortDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    LocalDateTime eventDate;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long views;
}
