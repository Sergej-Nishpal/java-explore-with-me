package ru.practicum.ewmmain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class EventShortDto {
    Long id;
    String title;
    String annotation;
    CategoryDto category;
    Boolean paid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Integer confirmedRequests;
    UserShortDto initiator;
    Long views;
}