package ru.practicum.ewmmain.controller.any;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventsRequestParameters {
    private String text;
    private Set<Long> categoryIds;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private String sort;
    private Integer from;
    private Integer size;
}