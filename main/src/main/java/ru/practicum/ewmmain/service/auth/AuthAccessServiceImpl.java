package ru.practicum.ewmmain.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.dto.incoming.NewEventDto;
import ru.practicum.ewmmain.dto.incoming.UpdateEventRequest;
import ru.practicum.ewmmain.dto.mapper.EntityMapper;
import ru.practicum.ewmmain.exception.CategoryNotFoundException;
import ru.practicum.ewmmain.exception.UserNotFoundException;
import ru.practicum.ewmmain.model.Event;
import ru.practicum.ewmmain.model.EventState;
import ru.practicum.ewmmain.repository.CategoryRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthAccessServiceImpl implements AuthAccessService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

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
        final Event event = Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> {
                    throw new CategoryNotFoundException(newEventDto.getCategory());
                }))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .location(newEventDto.getLocation())
                .eventDate(newEventDto.getEventDate())
                .initiator(userRepository.findById(userId).orElseThrow(() -> {
                    throw new UserNotFoundException(userId);
                }))
                .requestModeration(newEventDto.getRequestModeration())
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .build();
        return EntityMapper.toEventFullDto(eventRepository.save(event));
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