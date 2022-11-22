package ru.practicum.ewmmain.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.dto.incoming.NewEventDto;
import ru.practicum.ewmmain.dto.incoming.UpdateEventRequest;
import ru.practicum.ewmmain.repository.EventRepository;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthAccessServiceImpl implements AuthAccessService {
    private final EventRepository eventRepository;
    @Override
    public Collection<EventShortDto> getEventsCreatedByUserId(Long userId, Integer from, Integer size) {
        return Collections.emptyList();
    }

    @Override
    public EventFullDto updateEventByUserId(Long userId, UpdateEventRequest updateEventRequest) {
        return null;
    }

    @Override
    public EventFullDto addEventByUserId(Long userId, NewEventDto newEventDto) {
        return null;
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventFullDto cancelEventByUserIdAndEventId(Long userId, Long eventId) {
        return null;
    }

    @Override
    public Collection<ParticipationRequestDto> getParticipationRequestsOfEventId(Long userId, Long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto confirmParticipationRequestOfEventId(Long userId, Long eventId, Long reqId) {
        return null;
    }

    @Override
    public ParticipationRequestDto rejectParticipationRequestOfEventId(Long userId, Long eventId, Long reqId) {
        return null;
    }

    @Override
    public Collection<ParticipationRequestDto> getParticipationRequestsOfUserId(Long userId) {
        return null;
    }

    @Override
    public ParticipationRequestDto addParticipationRequestsOfUserId(Long userId, Long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequestId(Long userId, Long requestId) {
        return null;
    }
}
