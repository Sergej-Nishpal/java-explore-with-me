package ru.practicum.ewmmain.controller.any;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
@Jacksonized
public class EventsRequestParameters {
    String text;
    Set<Long> categoryIds;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Boolean onlyAvailable;
    Float lat;
    Float lon;
    EventSort sort;
    Integer from;
    Integer size;
}