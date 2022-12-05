package ru.practicum.ewmmain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmmain.model.LocationType;

import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class LocationFullDto {
    LocationType type;
    String description;
    Float lat;
    Float lon;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
}