package ru.practicum.ewmmain.service.any;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.controller.any.EventSort;
import ru.practicum.ewmmain.controller.any.EventsRequestParameters;
import ru.practicum.ewmmain.dto.*;
import ru.practicum.ewmmain.dto.incoming.GeoData;
import ru.practicum.ewmmain.dto.incoming.ViewStats;
import ru.practicum.ewmmain.dto.mapper.EntityMapper;
import ru.practicum.ewmmain.exception.CategoryNotFoundException;
import ru.practicum.ewmmain.exception.CompilationNotFoundException;
import ru.practicum.ewmmain.exception.EventNotFoundException;
import ru.practicum.ewmmain.exception.EventStateException;
import ru.practicum.ewmmain.model.*;
import ru.practicum.ewmmain.repository.CategoryRepository;
import ru.practicum.ewmmain.repository.CompilationRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.service.EventsUtility;
import ru.practicum.ewmmain.statclient.StatClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnyAccessServiceImpl implements AnyAccessService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CategoryRepository categoryRepository;
    private final StatClient statClient;
    private final EventsUtility eventsUtility;

    @Override
    public List<EventShortDto> getEvents(EventsRequestParameters parameters, EndpointHitDto endpointHitDto) {
        statClient.save(endpointHitDto);
        final QEvent event = QEvent.event;
        final Predicate textPredicate;
        if (parameters.getText() != null) {
            textPredicate = event.annotation.containsIgnoreCase(parameters.getText()).or(event.description
                    .containsIgnoreCase(parameters.getText()));
        } else {
            textPredicate = event.annotation.isNotEmpty()
                    .or(event.description.isNotEmpty());
        }

        final Predicate eventDatePredicate = parameters.getRangeStart() == null && parameters.getRangeEnd() == null
                ? event.eventDate.after(LocalDateTime.now())
                : event.eventDate.between(parameters.getRangeStart(), parameters.getRangeEnd());

        final Predicate predicate = event.category.id.in(parameters.getCategoryIds() != null
                        ? parameters.getCategoryIds()
                        : categoryRepository.findAll().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet()))
                .and(event.paid.in(parameters.getPaid() != null
                                ? Set.of(parameters.getPaid())
                                : Set.of(Boolean.TRUE, Boolean.FALSE))
                        .and(event.state.eq(EventState.PUBLISHED))
                        .and(eventDatePredicate)
                        .and(parameters.getOnlyAvailable().equals(true)
                                ? event.confirmedRequests.lt(event.participantLimit)
                                : null)
                        .and(textPredicate));

        final Pageable pageable;
        final int from = parameters.getFrom();
        final int size = parameters.getSize();

        if (parameters.getSort() != null && parameters.getSort().equals(EventSort.EVENT_DATE)) {
            pageable = PageRequest.of(from / size, size, Sort.by(EventSort.EVENT_DATE.toString()));
            return eventRepository.findAll(predicate, pageable).stream()
                    .map(EntityMapper::toEventShortDto)
                    .collect(Collectors.toList());
        } else if (parameters.getLat() != null && parameters.getLon() != null
                && parameters.getSort() != null && parameters.getSort().equals(EventSort.DISTANCE_KM)) {
            pageable = PageRequest.of( from / size, size);
            final float lat = parameters.getLat();
            final float lon = parameters.getLon();
            final List<EventShortDto> eventResult = eventsUtility.getEventsWithDistance(lat, lon, pageable);
            return eventsUtility.addViewsAndSortEventShortDtoList(eventResult);
        } else {
            pageable = PageRequest.of(from / size, size);
            final Page<Event> events = eventRepository.findAll(predicate, pageable);
            if (!events.isEmpty()) {
                return eventsUtility.getEventShortsWithViewsSorted(events);
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public List<EventShortDto> getEventsNearLocation(GeoData geoData,
                                                     Integer from,
                                                     Integer size,
                                                     EndpointHitDto endpointHitDto) {
        final Pageable pageable = PageRequest.of(from / size, size);
        final float lat = geoData.getLat();
        final float lon = geoData.getLon();
        final float radiusKm = geoData.getRadiusKm();
        return eventsUtility.getNearEvents(lat, lon, radiusKm, pageable);
    }

    @Override
    public EventFullDto getEventById(Long eventId, String ip, String path, String appName) {
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Событие с id = {} не найдено!", eventId);
            throw new EventNotFoundException(eventId);
        });
        if (event.getState().equals(EventState.PUBLISHED)) {
            final EventFullDto eventFullDto = EntityMapper.toEventFullDto(event);
            final LocalDateTime startDate = eventRepository
                    .findFirstByCategoryInOrderByCreatedOn(new HashSet<>(categoryRepository.findAll())).getCreatedOn();
            final List<ViewStats> viewStats = statClient.getStats(startDate, LocalDateTime.now(),
                    Set.of(path), false);
            long views = 0;
            if (!viewStats.isEmpty()) {
                for (ViewStats viewStat : viewStats) {
                    if (viewStat.getUri().equals(path)) {
                        views = viewStat.getHits();
                        break;
                    }
                }
                eventFullDto.setViews(views);
            }
            eventsUtility.saveHitOfViewedEvent(eventFullDto.getId(), appName, ip);
            return eventFullDto;
        } else {
            log.error("Событие с id = {} не опубликовано!", eventId);
            throw new EventStateException(String.format("Событие с id = %d не опубликовано!", eventId));
        }
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from / size, size);
        final QCompilation compilation = QCompilation.compilation;
        final Predicate predicate = compilation.pinned.in(pinned != null
                ? Set.of(pinned)
                : Set.of(Boolean.TRUE, Boolean.FALSE));
        return compilationRepository.findAll(predicate, pageable).stream()
                .map(EntityMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        final Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> {
            log.error("Подборка с id = {} не найдена!", compId);
            throw new CompilationNotFoundException(compId);
        });
        return EntityMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).stream()
                .map(EntityMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        final Category category = categoryRepository.findById(catId).orElseThrow(() -> {
            log.error("Категория с id = {} не найдена!", catId);
            throw new CategoryNotFoundException(catId);
        });
        return EntityMapper.toCategoryDto(category);
    }
}