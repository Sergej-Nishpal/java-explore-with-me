package ru.practicum.ewmmain.dto.incoming;

import lombok.Value;

import java.time.LocalDateTime;

@Value
// TODO Что оно тут делает?
public class EndpointHit {
    Long id;
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}