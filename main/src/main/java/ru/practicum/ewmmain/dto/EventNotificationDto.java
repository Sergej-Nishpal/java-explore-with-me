package ru.practicum.ewmmain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Value
@Builder
@ToString
@Jacksonized
@AllArgsConstructor
public class EventNotificationDto {
    String userEmail;
    String userName;
    String eventTitle;
    String locationDescription;
    Float eventDistanceKilometer;
    Float eventLat;
    Float eventLon;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
}