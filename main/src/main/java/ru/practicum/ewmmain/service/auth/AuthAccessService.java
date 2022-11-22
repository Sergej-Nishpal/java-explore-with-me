package ru.practicum.ewmmain.service.auth;

import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.dto.incoming.NewEventDto;
import ru.practicum.ewmmain.dto.incoming.UpdateEventRequest;

import java.util.Collection;

public interface AuthAccessService {
    Collection<EventShortDto> getEventsCreatedByUserId(Long userId, Integer from, Integer size);

    EventFullDto updateEventByUserId(Long userId, UpdateEventRequest updateEventRequest);

    EventFullDto addEventByUserId(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto cancelEventByUserIdAndEventId(Long userId, Long eventId);

    Collection<ParticipationRequestDto> getParticipationRequestsOfEventId(Long userId, Long eventId);

    ParticipationRequestDto confirmParticipationRequestOfEventId(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto rejectParticipationRequestOfEventId(Long userId, Long eventId, Long reqId);

    Collection<ParticipationRequestDto> getParticipationRequestsOfUserId(Long userId);

    ParticipationRequestDto addParticipationRequestsOfUserId(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipationRequestId(Long userId, Long requestId);
}