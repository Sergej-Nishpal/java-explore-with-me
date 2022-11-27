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
import ru.practicum.ewmmain.model.*;
import ru.practicum.ewmmain.repository.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthAccessServiceImpl implements AuthAccessService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
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
                throw new IncorrectEventStateException("Изменить можно только отмененные события или " +
                        "события в состоянии ожидания модерации!");
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
        final Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> {
            throw new CategoryNotFoundException(newEventDto.getCategory());
        });

        final User initiator = userRepository.findById(userId).orElseThrow(() -> {
            throw new UserNotFoundException(userId);
        });

        final Event event = EntityMapper.toEvent(newEventDto, category, location, initiator);
        return EntityMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        final Event event = eventRepository.findAllByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new EventNotFoundException(eventId);
        } else {
            return EntityMapper.toEventFullDto(event);
        }
    }

    @Override
    @Transactional
    public EventFullDto cancelEventByUserIdAndEventId(Long userId, Long eventId) {
        //Отменить можно только событие в состоянии ожидания модерации.
        final Event event = eventRepository.findAllByIdAndInitiatorId(eventId, userId);

        if (event == null) {
            throw new EventNotFoundException(eventId);
        } else {
            if (!event.getInitiator().getId().equals(userId)) {
                throw new UnauthorisedAccessException(String.format("Пользователь с id = %d пытается отменить " +
                        "чужое событие с id = %d!", userId, eventId));
            }

            if (!event.getState().equals(EventState.PENDING)) {
                throw new IncorrectEventStateException("Отменить можно только событие в состоянии ожидания модерации");
            } else {
                event.setState(EventState.CANCELED);
                return EntityMapper.toEventFullDto(eventRepository.save(event));
            }
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto addParticipationRequestsOfUserId(Long userId, Long eventId) {
        //нельзя добавить повторный запрос //TODO отработает за счёт уникальности в БД?
        //инициатор события не может добавить запрос на участие в своём событии
        //нельзя участвовать в неопубликованном событии
        //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку
        //если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new EventNotFoundException(eventId);
        });

        if (event.getInitiator().getId().equals(userId)) {
            throw new ParticipationRequestException("Инициатор события не может добавить запрос на участие " +
                    "в своём событии");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ParticipationRequestException("Нельзя участвовать в неопубликованном событии!");
        }

        if (event.getConfirmedRequests().intValue() == event.getParticipantLimit().intValue()) {
            throw new ParticipationRequestException("У события достигнут лимит запросов на участие!");
        }

        final ParticipationRequest participationRequest = ParticipationRequest.builder()
                .requesterId(userId)
                .eventId(eventId)
                .created(LocalDateTime.now())
                .status(Boolean.FALSE.equals(event.getRequestModeration())
                        ? ParticipationRequestStatus.CONFIRMED
                        : ParticipationRequestStatus.PENDING)
                .build();
        return EntityMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequestId(Long userId, Long requestId) {
        final ParticipationRequest participationRequest = requestRepository.findById(requestId).orElseThrow(() -> {
            throw new ParticipationRequestException(String.format("Запрос с id = %d не найден!", requestId));
        });

        if (!participationRequest.getRequesterId().equals(userId)) {
            throw new UnauthorisedAccessException(String.format("Пользователь с id = %d пытается отменить " +
                    "чужой запрос на участие в событии с id = %d!", userId, requestId));
        }

        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        return EntityMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Override
    public ParticipationRequestDto confirmParticipationRequestOfEventId(Long userId, Long eventId, Long reqId) {
        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие
        //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new EventNotFoundException(eventId);
        });

        if (!event.getInitiator().getId().equals(userId)) {
            throw new UnauthorisedAccessException(String.format("Пользователь с id = %d пытается подтвердить " +
                    "чужой запрос на чужое событие с id = %d!", userId, eventId));
        }

        final ParticipationRequest participationRequest = requestRepository.findById(reqId).orElseThrow(() -> {
            throw new ParticipationRequestException(String.format("Запрос с id = %d не найден!", reqId));
        });

        if (event.getParticipantLimit() == 0 || Boolean.TRUE.equals(!event.getRequestModeration())) {
            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
        } else {
            // TODO Непонятно, что тут дальше? Что делать после "не требуется"?
        }
        return null;
    }

    @Override
    public ParticipationRequestDto rejectParticipationRequestOfEventId(Long userId, Long eventId, Long reqId) {
        return null;
    }

    @Override
    public Collection<ParticipationRequestDto> getParticipationRequestsOfEventId(Long userId, Long eventId) {
        return null;
    }

    @Override
    public Collection<ParticipationRequestDto> getParticipationRequestsOfUserId(Long userId) {
        return null;
    }
}