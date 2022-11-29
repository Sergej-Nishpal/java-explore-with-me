package ru.practicum.ewmmain.dto.incoming;

import lombok.Value;

import java.time.LocalDateTime;

@Value

public class EndpointHit {
    Long id;
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}