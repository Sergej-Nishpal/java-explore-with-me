package ru.practicum.ewmmain.service.admin;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.*;
import ru.practicum.ewmmain.dto.incoming.*;
import ru.practicum.ewmmain.dto.mapper.EntityMapper;
import ru.practicum.ewmmain.exception.*;
import ru.practicum.ewmmain.model.*;
import ru.practicum.ewmmain.repository.*;
import ru.practicum.ewmmain.service.EventsUtility;
import ru.practicum.ewmmain.statclient.StatClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAccessServiceImpl implements AdminAccessService {
    private static final int MIN_HOURS_BEFORE_EVENT_DATE = 1;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;
    private final LocationRepository locationRepository;
    private final EventsUtility eventsUtility;
    private final StatClient statClient;

    @Override
    public List<EventFullDto> getEvents(Set<Long> users, Set<EventState> states,
                                        Set<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size) {
        final QEvent event = QEvent.event;
        final Predicate eventDatePredicate = rangeStart == null && rangeEnd == null
                ? event.eventDate.after(LocalDateTime.now())
                : event.eventDate.between(rangeStart, rangeEnd);
        final Predicate predicate = event.initiator.id.in(users != null ? users : Collections.emptySet())
                .and(event.state.in(states != null ? states : Collections.emptySet()))
                .and(event.category.id.in(categories != null ? categories : Collections.emptySet()))
                .and(eventDatePredicate);
        final Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAll(predicate, pageable)
                .stream()
                .map(EntityMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(Long eventId, AdminUpdateEventRequest request) {
        final Event savedEvent = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new EventNotFoundException(eventId);
        });
        final Event eventToUpdate = Event.builder()
                .title(request.getTitle() != null
                        ? request.getTitle()
                        : savedEvent.getTitle())
                .annotation(request.getAnnotation() != null
                        ? request.getAnnotation()
                        : savedEvent.getAnnotation())
                .description(request.getDescription() != null
                        ? request.getDescription()
                        : savedEvent.getDescription())
                .category(request.getCategory() != null
                        ? getCategoryWithCheck(request.getCategory())
                        : savedEvent.getCategory())
                .paid(request.getPaid() != null
                        ? request.getPaid()
                        : savedEvent.getPaid())
                .participantLimit(request.getParticipantLimit() != null
                        ? request.getParticipantLimit()
                        : savedEvent.getParticipantLimit())
                .location(request.getLocation() != null
                        ? request.getLocation()
                        : savedEvent.getLocation())
                .eventDate(request.getEventDate() != null
                        ? request.getEventDate()
                        : savedEvent.getEventDate())
                .initiator(savedEvent.getInitiator())
                .requestModeration(request.getRequestModeration() != null
                        ? request.getRequestModeration()
                        : savedEvent.getRequestModeration())
                .createdOn(savedEvent.getCreatedOn())
                .state(savedEvent.getState())
                .publishedOn(savedEvent.getPublishedOn())
                .confirmedRequests(savedEvent.getConfirmedRequests())
                .build();
        return EntityMapper.toEventFullDto(eventRepository.save(eventToUpdate));
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(Long eventId) {
        Event event = getEventWithCheck(eventId);
        if (!event.getEventDate().minusHours(MIN_HOURS_BEFORE_EVENT_DATE).isAfter(LocalDateTime.now())) {
            throw new IncorrectEventDateException(MIN_HOURS_BEFORE_EVENT_DATE);
        }

        if (!event.getState().equals(EventState.PENDING)) {
            throw new EventStateException("Событие должно быть в состоянии ожидания публикации!");
        }

        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        final Event publishedEvent = eventRepository.save(event);
        statClient.mail(eventsUtility.getNotificationList(publishedEvent, 3.0f));
        return EntityMapper.toEventFullDto(publishedEvent);
    }

    @Override
    @Transactional
    public EventFullDto rejectEvent(Long eventId) {
        Event event = getEventWithCheck(eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateException("Событие не должно быть опубликовано!");
        }

        event.setState(EventState.CANCELED);
        return EntityMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public CategoryDto changeCategory(CategoryDto categoryDto) {
        Category category = getCategoryWithCheck(categoryDto.getId());
        category.setName(categoryDto.getName());
        return EntityMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        final Category category = categoryRepository.save(EntityMapper.toCategory(newCategoryDto));
        return EntityMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long catId) {
        final Category category = categoryRepository.findById(catId).orElseThrow(() -> {
            throw new CategoryNotFoundException(catId);
        });
        categoryRepository.deleteById(category.getId());
    }

    @Override
    public List<UserDto> getUsers(Set<Long> ids, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from / size, size);
        final Page<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findAllByIdIn(ids, pageable);
        }

        return users.stream()
                .map(EntityMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        Location userLocation = null;
        if (newUserRequest.getLat() != null && newUserRequest.getLon() != null) {
            userLocation = Location.builder()
                    .type(LocationType.PRIVATE)
                    .description(newUserRequest.getName() + "\n" + newUserRequest.getEmail())
                    .lat(newUserRequest.getLat())
                    .lon(newUserRequest.getLon())
                    .createdOn(LocalDateTime.now())
                    .build();
            locationRepository.save(userLocation);
        }
        final User user = userRepository.save(EntityMapper.toUser(newUserRequest, userLocation));
        return EntityMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        final QEvent event = QEvent.event;
        final Predicate predicate = event.id.in(newCompilationDto.getEvents());
        final Iterable<Event> events = eventRepository.findAll(predicate);
        final Set<Event> eventSet = new HashSet<>();
        events.iterator().forEachRemaining(eventSet::add);
        final Compilation compilation = compilationRepository
                .save(EntityMapper.toCompilation(newCompilationDto, eventSet));
        return EntityMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        final Compilation compilation = getCompilationWithCheck(compId);
        final Event event = getEventWithCheck(eventId);
        final Set<Event> events = compilation.getEvents();
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void addEventToCompilation(Long compId, Long eventId) {
        final Compilation compilation = getCompilationWithCheck(compId);
        final Event event = getEventWithCheck(eventId);
        final Set<Event> events = compilation.getEvents();
        events.add(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void unpinCompilationById(Long compId) {
        Compilation compilation = getCompilationWithCheck(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void pinCompilationById(Long compId) {
        Compilation compilation = getCompilationWithCheck(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Override
    public LocationFullDto addLocation(LocationDto locationDto) {
        final Location location = locationRepository.save(EntityMapper.toLocation(locationDto));
        return EntityMapper.toLocationFullDto(location);
    }

    @Override
    public LocationFullDto changeLocation(Long locId, LocationDto locationDto) {
        final Location savedLocation = locationRepository.findById(locId).orElseThrow(() -> {
            log.error("Локация с id = {} не найдена!", locId);
            throw new LocationNotFoundException(String.format("Локация с id = %d не найдена!", locId));
        });
            final Location locationToUpdate = Location.builder()
                    .id(locId)
                    .type(locationDto.getType() != savedLocation.getType()
                            ? locationDto.getType()
                            : savedLocation.getType())
                    .description(!locationDto.getDescription().equals(savedLocation.getDescription())
                            ? locationDto.getDescription()
                            : savedLocation.getDescription())
                    .lat(!locationDto.getLat().equals(savedLocation.getLat())
                            ? locationDto.getLat()
                            : savedLocation.getLat())
                    .lon(!locationDto.getLon().equals(savedLocation.getLon())
                            ? locationDto.getLon()
                            : savedLocation.getLon())
                    .createdOn(savedLocation.getCreatedOn())
                    .build();
        return EntityMapper.toLocationFullDto(locationRepository.save(locationToUpdate));
    }

    @Override
    public void deleteLocation(Long locId) {
        locationRepository.deleteById(locId);
    }

    @Override
    public List<LocationFullDto> getLocations(Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from / size, size);
        return locationRepository.findAll(pageable)
                .stream()
                .map(EntityMapper::toLocationFullDto)
                .collect(Collectors.toList());
    }

    private Category getCategoryWithCheck(long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Категория с id = {} не найдена!", categoryId);
            throw new CategoryNotFoundException(categoryId);
        });
    }

    private Event getEventWithCheck(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Событие с id = {} не найдено!", eventId);
            throw new EventNotFoundException(eventId);
        });
    }

    private Compilation getCompilationWithCheck(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> {
            log.error("Подборка с id = {} не найдена!", compId);
            throw new CompilationNotFoundException(compId);
        });
    }
}