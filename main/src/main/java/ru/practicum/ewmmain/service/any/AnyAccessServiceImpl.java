package ru.practicum.ewmmain.service.any;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.controller.any.EventsRequestParameters;
import ru.practicum.ewmmain.dto.*;
import ru.practicum.ewmmain.dto.incoming.ViewStats;
import ru.practicum.ewmmain.dto.mapper.EntityMapper;
import ru.practicum.ewmmain.exception.CategoryNotFoundException;
import ru.practicum.ewmmain.exception.CompilationNotFoundException;
import ru.practicum.ewmmain.model.*;
import ru.practicum.ewmmain.repository.CategoryRepository;
import ru.practicum.ewmmain.repository.CompilationRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.statclient.StatClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnyAccessServiceImpl implements AnyAccessService {
    private static final String EVENT_DATE_SORT = "eventDate";
    private static final String EVENTS_PATH = "events";

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CategoryRepository categoryRepository;
    private final StatClient statClient;

    @Override
    public List<EventShortDto> getEvents(EventsRequestParameters parameters, String ip,
                                         String path, String appName) {
        final LocalDateTime localDateTimeToSave = LocalDateTime.now();
        final EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(path)
                .ip(ip)
                .timestamp(localDateTimeToSave)
                .build();

        statClient.save(endpointHitDto);
        final QEvent event = QEvent.event;

        final Predicate textPredicate;
        if (parameters.getText() != null) {
            textPredicate = event.annotation.containsIgnoreCase(parameters.getText()).or(event.description
                    .containsIgnoreCase(parameters.getText()));
        } else {
            textPredicate = event.annotation.containsIgnoreCase("").or(event.description
                    .containsIgnoreCase(""));
        }

        final Predicate eventDatePredicate = parameters.getRangeStart() == null && parameters.getRangeEnd() == null
                ? event.eventDate.after(LocalDateTime.now())
                : event.eventDate.between(parameters.getRangeStart(), parameters.getRangeEnd());

        final Predicate predicate = event.category.id.in(
                        parameters.getCategoryIds() != null
                                ? parameters.getCategoryIds()
                                : Collections.emptySet())
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
        final List<EventShortDto> eventsResult;

        if (parameters.getSort() != null && Objects.equals(parameters.getSort(), "EVENT_DATE")) {
            pageable = PageRequest.of(parameters.getFrom() / parameters.getSize(),
                    parameters.getSize(), Sort.by(EVENT_DATE_SORT));
            eventsResult = getEventShorts(parameters, predicate, pageable);
            return eventsResult;
        } else {
            pageable = PageRequest.of(parameters.getFrom() / parameters.getSize(),
                    parameters.getSize());
            eventsResult = getEventShorts(parameters, predicate, pageable);
            return eventsResult.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public EventFullDto getEventById(Long eventId, String ip, String path, String appName) {
        final Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED);
        final List<ViewStats> stats = statClient.getStats(LocalDateTime.now().minusYears(1), LocalDateTime.now(),
                Set.of(path), false);
        long views = 0;
        for (ViewStats viewStats : stats) {
            if (viewStats.getUri().equals(path)) {
                views = viewStats.getHits();
                break;
            }
        }

        EventFullDto eventFullDto = EntityMapper.toEventFullDto(event);
        eventFullDto.setViews(views);
        saveHitOfViewedEvent(eventFullDto.getId(), appName, ip);
        return eventFullDto;
    }

    private void saveHitOfViewedEvent(long eventId, String appName, String ip) {
        final EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(appName)
                .uri("/" + EVENTS_PATH + "/" + eventId)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        statClient.save(endpointHitDto);
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

    private List<EventShortDto> getEventShorts(EventsRequestParameters parameters,
                                               Predicate predicate, Pageable pageable) {
        Page<Event> events = eventRepository.findAll(predicate, pageable);
        Set<String> uris = events.stream()
                .map(e -> ("/" + EVENTS_PATH + "/" + e.getId()))
                .collect(Collectors.toSet());
        final LocalDateTime start;
        final LocalDateTime end;
        if (parameters.getRangeStart() == null && parameters.getRangeEnd() == null) {
            start = LocalDateTime.now().minusYears(1);
            end = LocalDateTime.now();
        } else {
            start = parameters.getRangeStart();
            end = parameters.getRangeEnd();
        }
        List<ViewStats> viewStats = statClient.getStats(start, end, uris, false);
        List<EventShortDto> eventsResult = events.stream()
                .map(EntityMapper::toEventShortDto)
                .collect(Collectors.toList());
        for (EventShortDto eventShortDto : eventsResult) {
            for (ViewStats view : viewStats) {
                if (view.getUri().contains(eventShortDto.getId().toString())) {
                    eventShortDto.setViews(view.getHits());
                }
            }
        }

        return eventsResult;
    }
}