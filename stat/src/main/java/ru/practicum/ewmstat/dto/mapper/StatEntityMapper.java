package ru.practicum.ewmstat.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewmstat.dto.EndpointHitDto;
import ru.practicum.ewmstat.dto.EventNotificationDto;
import ru.practicum.ewmstat.model.EndpointHit;
import ru.practicum.ewmstat.model.EventNotification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatEntityMapper {

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }

    public static List<EventNotification> toEventNotifications(List<EventNotificationDto> notifications) {
        return notifications.stream()
                .map(StatEntityMapper::toEventNotification)
                .collect(Collectors.toList());
    }

    public static EventNotification toEventNotification(EventNotificationDto eventNotificationDto) {
        return EventNotification.builder()
                .userEmail(eventNotificationDto.getUserEmail())
                .userName(eventNotificationDto.getUserName())
                .eventTitle(eventNotificationDto.getEventTitle())
                .locationDescription(eventNotificationDto.getLocationDescription())
                .eventDistanceKilometer(eventNotificationDto.getEventDistanceKilometer())
                .eventLat(eventNotificationDto.getEventLat())
                .eventLon(eventNotificationDto.getEventLon())
                .eventDate(eventNotificationDto.getEventDate())
                .createdAt(LocalDateTime.now()).build();
    }
}