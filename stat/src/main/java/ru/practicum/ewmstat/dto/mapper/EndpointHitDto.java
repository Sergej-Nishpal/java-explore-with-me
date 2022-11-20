package ru.practicum.ewmstat.dto.mapper;

import lombok.*;

import java.time.LocalDateTime;

@Value
@Builder
public class EndpointHitDto {
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}
