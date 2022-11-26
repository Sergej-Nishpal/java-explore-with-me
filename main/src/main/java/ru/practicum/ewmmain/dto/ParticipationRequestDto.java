package ru.practicum.ewmmain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmmain.model.ParticipationRequestStatus;

import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class ParticipationRequestDto {
    Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;

    Long event;
    Long requester;
    ParticipationRequestStatus status;
}