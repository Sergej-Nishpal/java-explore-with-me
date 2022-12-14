package ru.practicum.ewmmain.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.ewmmain.model.*;
import ru.practicum.ewmmain.repository.*;
import ru.practicum.ewmmain.service.EventsUtility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthAccessServiceImpl implements AuthAccessService {
    private static final int MIN_HOURS_BEFORE_EVENT_DATE = 2;

    private static final String EVENT_NOT_FOUND = "Событие с id = {} не найдено!";

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final EventsUtility eventsUtility;

    @Override
    public List<EventShortDto> getEventsCreatedByUserId(Long userId, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageable)
                .stream()
                .map(EntityMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsNearMe(Long userId, Float radiusKm, Integer from, Integer size) {
        final User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException(userId);
        });
        final Location userLocation;
        if (user.getLocationId() != null) {
            userLocation = locationRepository.findById(user.getLocationId()).orElseThrow(() -> {
                throw new LocationNotFoundException(String.format("Локация с id = %d не найдена!",
                        user.getLocationId()));
            });
        } else {
            throw new LocationNotFoundException(String.format("Пользователь с id = %d " +
                    "не указал свою локацию при регистрации!", userId));
        }

        final float lat = userLocation.getLat();
        final float lon = userLocation.getLon();
        final Pageable pageable = PageRequest.of(from / size, size);
        final List<EventShortDto> eventsResult = eventsUtility.getNearEvents(lat, lon, radiusKm, pageable);
        return eventsUtility.addViewsAndSortEventShortDtoList(eventsResult);
    }

    @Override
    @Transactional
    public EventFullDto addEventByUserId(Long userId, NewEventDto newEventDto) {
        if (!newEventDto.getEventDate().minusHours(MIN_HOURS_BEFORE_EVENT_DATE).isAfter(LocalDateTime.now())) {
            log.error("Некорректная дата для добавления события!");
            throw new IncorrectEventDateException(MIN_HOURS_BEFORE_EVENT_DATE);
        }

        final Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> {
            log.error("Категория с id = {} не найдена!", newEventDto.getCategory());
            throw new CategoryNotFoundException(newEventDto.getCategory());
        });

        final User initiator = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь с id = {} не найден!", userId);
            throw new UserNotFoundException(userId);
        });

        final LocationDto locationDto = LocationDto.builder()
                .type(newEventDto.getLocation().getType() != null
                        ? newEventDto.getLocation().getType()
                        : LocationType.OTHER)
                .description(newEventDto.getLocation().getDescription() != null
                        ? newEventDto.getLocation().getDescription()
                        : "Some description")
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .build();
        final Location locationToSave;
        final Location locationWithSameLatLon = locationRepository
                .findByLatAndLon(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon());
        if (locationWithSameLatLon == null
                || (!locationWithSameLatLon.getType().equals(locationDto.getType())
                && !locationWithSameLatLon.getDescription().equals(locationDto.getDescription())
                && locationWithSameLatLon.getLat() != locationDto.getLat()
                && locationWithSameLatLon.getLon() != locationDto.getLon())) {
            log.debug("Добавление новой локации с описанием \"{}\".", locationDto.getDescription());
            locationToSave = locationRepository.save(EntityMapper.toLocation(locationDto));
        } else {
            locationToSave = locationWithSameLatLon;
        }

        final Event event = EntityMapper.toEvent(newEventDto, category, locationToSave, initiator);
        return EntityMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUserId(Long userId, UpdateEventRequest request) {
        if (!request.getEventDate().minusHours(MIN_HOURS_BEFORE_EVENT_DATE).isAfter(LocalDateTime.now())) {
            log.error("Некорректная дата для обновления события!");
            throw new IncorrectEventDateException(MIN_HOURS_BEFORE_EVENT_DATE);
        }

        final Event savedEvent = eventRepository.findAllByIdAndInitiatorId(request.getEventId(), userId);
        final Event eventToUpdate;
        if (savedEvent != null) {
            if (savedEvent.getState().equals(EventState.PUBLISHED)) {
                log.error("Попытка изменить опубликованное событие!");
                throw new IncorrectEventStateException("Изменить можно только отмененные события или " +
                        "события в состоянии ожидания модерации!");
            }

            if (savedEvent.getState().equals(EventState.CANCELED)) {
                savedEvent.setState(EventState.PENDING);
            }

            eventToUpdate = getEventToUpdate(request, savedEvent);
        } else {
            log.error(EVENT_NOT_FOUND, request.getEventId());
            throw new EventNotFoundException(request.getEventId());
        }

        return EntityMapper.toEventFullDto(eventRepository.save(eventToUpdate));
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        final Event event = eventRepository.findAllByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            log.error(EVENT_NOT_FOUND, eventId);
            throw new EventNotFoundException(eventId);
        } else {
            return EntityMapper.toEventFullDto(event);
        }
    }

    @Override
    @Transactional
    public EventFullDto cancelEventByUserIdAndEventId(Long userId, Long eventId) {
        final Event event = eventRepository.findAllByIdAndInitiatorId(eventId, userId);

        if (event == null) {
            log.error(EVENT_NOT_FOUND, eventId);
            throw new EventNotFoundException(eventId);
        } else {
            if (!event.getInitiator().getId().equals(userId)) {
                log.error("Пользователь с id = {} пытается отменить чужое событие с id = {}!", userId, eventId);
                throw new UnauthorisedAccessException(String.format("Пользователь с id = %d пытается отменить " +
                        "чужое событие с id = %d!", userId, eventId));
            }

            if (!event.getState().equals(EventState.PENDING)) {
                log.error("Попытка отменить событие не в состоянии модерации!");
                throw new IncorrectEventStateException("Отменить можно только событие в состоянии ожидания модерации!");
            } else {
                event.setState(EventState.CANCELED);
                return EntityMapper.toEventFullDto(eventRepository.save(event));
            }
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto addParticipationRequestsOfUserId(Long userId, Long eventId) {
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error(EVENT_NOT_FOUND, eventId);
            throw new EventNotFoundException(eventId);
        });

        if (event.getInitiator().getId().equals(userId)) {
            log.error("Попытка добавить запрос на участие в своём событии!");
            throw new ParticipationRequestException("Инициатор события не может добавить запрос на участие " +
                    "в своём событии!");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("Попытка участвовать в неопубликованном событии!");
            throw new ParticipationRequestException("Нельзя участвовать в неопубликованном событии!");
        }

        if (event.getConfirmedRequests().intValue() == event.getParticipantLimit().intValue()) {
            log.error("У события достигнут лимит запросов на участие!");
            throw new ParticipationRequestException("У события достигнут лимит запросов на участие!");
        }

        final ParticipationRequest participationRequest = ParticipationRequest.builder()
                .requesterId(userId)
                .eventId(eventId)
                .created(LocalDateTime.now())
                .status((Boolean.FALSE.equals(event.getRequestModeration()) || event.getParticipantLimit() == 0)
                        ? ParticipationRequestStatus.CONFIRMED
                        : ParticipationRequestStatus.PENDING)
                .build();
        return EntityMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequestId(Long userId, Long requestId) {
        final ParticipationRequest participationRequest = requestRepository.findById(requestId).orElseThrow(() -> {
            log.error("Заявка с id = {} не найдена!", requestId);
            throw new ParticipationRequestNotFoundException(requestId);
        });

        if (!participationRequest.getRequesterId().equals(userId)) {
            log.error("Пользователь с id = {} пытается отменить " +
                    "чужой запрос на участие в событии с id = {}!", userId, requestId);
            throw new UnauthorisedAccessException(String.format("Пользователь с id = %d пытается отменить " +
                    "чужой запрос на участие в событии с id = %d!", userId, requestId));
        }

        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        return EntityMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto confirmParticipationRequestOfEventId(Long userId, Long eventId, Long reqId) {
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error(EVENT_NOT_FOUND, eventId);
            throw new EventNotFoundException(eventId);
        });

        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Пользователь с id = {} пытается подтвердить " +
                    "чужой запрос на чужое событие с id = {}!", userId, eventId);
            throw new UnauthorisedAccessException(String.format("Пользователь с id = %d пытается подтвердить " +
                    "чужой запрос на чужое событие с id = %d!", userId, eventId));
        }

        ParticipationRequest participationRequest = requestRepository.findById(reqId).orElseThrow(() -> {
            log.error("Запрос с id = {} не найден!", reqId);
            throw new ParticipationRequestException(String.format("Запрос с id = %d не найден!", reqId));
        });

        if (Boolean.FALSE.equals(event.getRequestModeration()) || event.getParticipantLimit() == 0) {
            return EntityMapper.toParticipationRequestDto(participationRequest);

        } else {
            if (event.getConfirmedRequests().intValue() == event.getParticipantLimit().intValue()) {
                log.error("На событие с id = {} достигнут лимит по заявкам!", eventId);
                throw new ParticipationRequestException(String.format("На событие с id = %d " +
                        "достигнут лимит по заявкам!", eventId));
            }

            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
            participationRequest = requestRepository.save(participationRequest);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);

            if (event.getConfirmedRequests().intValue() == event.getParticipantLimit().intValue()) {
                final List<ParticipationRequest> pendingRequests = requestRepository
                        .findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.PENDING);
                pendingRequests.forEach(request -> request.setStatus(ParticipationRequestStatus.REJECTED));
                requestRepository.saveAll(pendingRequests);
            }
        }
        return EntityMapper.toParticipationRequestDto(participationRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectParticipationRequestOfEventId(Long userId, Long eventId, Long reqId) {
        final ParticipationRequest participationRequest = requestRepository.findById(reqId).orElseThrow(() -> {
            log.error("Запрос с id = {} не найден!", reqId);
            throw new ParticipationRequestException(String.format("Запрос с id = %d не найден!", reqId));
        });

        participationRequest.setStatus(ParticipationRequestStatus.REJECTED);
        return EntityMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsOfEventId(Long userId, Long eventId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Пользователь с id = {} не найден!", userId);
            throw new UserNotFoundException(userId);
        });
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error(EVENT_NOT_FOUND, eventId);
            throw new EventNotFoundException(eventId);
        });
        if (!event.getInitiator().getId().equals(user.getId())) {
            log.error("Пользователь с id = {} пытается получить " +
                    "перечень запросов на чужое событие с id = {}!", userId, eventId);
            throw new UnauthorisedAccessException(String.format("Пользователь с id = %d пытается получить " +
                    "перечень запросов на чужое событие с id = %d!", userId, eventId));
        }
        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(EntityMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsOfUserId(Long userId) {
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(EntityMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    private Event getEventToUpdate(UpdateEventRequest request, Event savedEvent) {
        return Event.builder()
                .title(request.getTitle() != null
                        ? request.getTitle()
                        : savedEvent.getTitle())
                .annotation(request.getAnnotation() != null
                        ? request.getAnnotation()
                        : savedEvent.getAnnotation())
                .description(request.getDescription() != null
                        ? request.getDescription()
                        : savedEvent.getDescription())
                .category(request.getCategoryId() != null
                        ? categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> {
                    log.error("Категория с id = {} не найдена!", request.getCategoryId());
                    throw new CategoryNotFoundException(request.getCategoryId());
                })
                        : savedEvent.getCategory())
                .paid(request.getPaid() != null
                        ? request.getPaid()
                        : savedEvent.getPaid())
                .participantLimit(request.getParticipantLimit() != null
                        ? request.getParticipantLimit()
                        : savedEvent.getParticipantLimit())
                .eventDate(request.getEventDate())
                .location(savedEvent.getLocation())
                .initiator(savedEvent.getInitiator())
                .requestModeration(savedEvent.getRequestModeration())
                .createdOn(savedEvent.getCreatedOn())
                .state(savedEvent.getState())
                .publishedOn(savedEvent.getPublishedOn())
                .confirmedRequests(savedEvent.getConfirmedRequests())
                .build();
    }
}