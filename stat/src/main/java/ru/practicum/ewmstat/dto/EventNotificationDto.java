package ru.practicum.ewmstat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Value
@Builder
@ToString
@Jacksonized
@AllArgsConstructor
public class EventNotificationDto {

    @Email
    @NotBlank
    String userEmail;

    @NotBlank
    String userName;

    @NotBlank
    String eventTitle;

    @NotBlank
    String locationDescription;

    @PositiveOrZero
    Float eventDistanceKilometer;

    @NotNull
    Float eventLat;

    @NotNull
    Float eventLon;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
}