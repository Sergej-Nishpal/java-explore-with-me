package ru.practicum.ewmmain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmmain.model.Category;
import ru.practicum.ewmmain.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Jacksonized
@AllArgsConstructor
public class EventLocDto {
    Long id;
    String title;
    String annotation;
    Category category;
    Boolean paid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Integer confirmedRequests;
    User initiator;
    Float distanceKm;
}