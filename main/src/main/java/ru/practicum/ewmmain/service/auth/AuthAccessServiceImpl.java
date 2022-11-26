package ru.practicum.ewmmain.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.dto.incoming.LocationDto;
import ru.practicum.ewmmain.dto.incoming.NewEventDto;
import ru.practicum.ewmmain.dto.incoming.UpdateEventRequest;
import ru.practicum.ewmmain.dto.mapper.EntityMapper;
import ru.practicum.ewmmain.exception.*;
import ru.practicum.ewmmain.model.Event;
import ru.practicum.ewmmain.model.EventState;
import ru.practicum.ewmmain.model.Location;
import ru.practicum.ewmmain.repository.CategoryRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.repository.LocationRepository;
import ru.practicum.ewmmain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthAccessServiceImpl implements AuthAccessService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    @Override
    public Collection<EventShortDto> getEventsCreatedByUserId(Long userId, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageable)
                .stream()
                .map(EntityMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUserId(Long userId, UpdateEventRequest updateEventRequest) {
        //изменить можно только отмененные события или события в состоянии ожидания модерации
        //если редактируется отменённое событие, то оно автоматически переходит в состояние ожидания модерации
        //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
        final int hoursBeforeForUpdateEvent = 2;
        final Event event = eventRepository.findAllByIdAndInitiatorId(userId, updateEventRequest.getEventId());

        if (event != null) {
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new IncorrectEventStateException(EventState.PUBLISHED.name());
            }

            if (!event.getEventDate().minusHours(hoursBeforeForUpdateEvent).isAfter(LocalDateTime.now())) {
                throw new IncorrectEventDateException(hoursBeforeForUpdateEvent);
            }

            if (event.getState().equals(EventState.CANCELED)) {
                event.setState(EventState.PENDING);
            }

            event.setTitle(updateEventRequest.getTitle());
            event.setAnnotation(updateEventRequest.getAnnotation());
            event.setDescription(updateEventRequest.getDescription());
            event.setCategory(categoryRepository.findById(updateEventRequest.getCategoryId()).orElseThrow(() -> {
                throw new CategoryNotFoundException(updateEventRequest.getCategoryId());
            }));
            event.setPaid(updateEventRequest.getPaid());
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
            event.setEventDate(updateEventRequest.getEventDate());
        } else {
            throw new EventNotFoundException(updateEventRequest.getEventId());
        }

        return EntityMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto addEventByUserId(Long userId, NewEventDto newEventDto) {
        //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
        final int hoursBeforeForAddEvent = 2;

        if (!newEventDto.getEventDate().minusHours(hoursBeforeForAddEvent).isAfter(LocalDateTime.now())) {
            throw new IncorrectEventDateException(hoursBeforeForAddEvent);
        }

        final LocationDto locationDto = LocationDto.builder()
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .build();

        final Location location = locationRepository.save(EntityMapper.toLocation(locationDto));

        final Event event = Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> {
                    throw new CategoryNotFoundException(newEventDto.getCategory());
                }))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .location(location)
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