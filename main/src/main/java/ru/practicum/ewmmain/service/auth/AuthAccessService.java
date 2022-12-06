package ru.practicum.ewmmain.service.auth;

import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.dto.incoming.NewEventDto;
import ru.practicum.ewmmain.dto.incoming.UpdateEventRequest;

import java.util.List;

public interface AuthAccessService {
    List<EventShortDto> getEventsCreatedByUserId(Long userId, Integer from, Integer size);

    List<EventShortDto> getEventsNearMe(Long userId, Float radiusKm, Integer from, Integer size);

    EventFullDto updateEventByUserId(Long userId, UpdateEventRequest updateEventRequest);

    EventFullDto addEventByUserId(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto cancelEventByUserIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequestDto> getParticipationRequestsOfEventId(Long userId, Long eventId);

    ParticipationRequestDto confirmParticipationRequestOfEventId(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto rejectParticipationRequestOfEventId(Long userId, Long eventId, Long reqId);

    List<ParticipationRequestDto> getParticipationRequestsOfUserId(Long userId);

    ParticipationRequestDto addParticipationRequestsOfUserId(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipationRequestId(Long userId, Long requestId);
}