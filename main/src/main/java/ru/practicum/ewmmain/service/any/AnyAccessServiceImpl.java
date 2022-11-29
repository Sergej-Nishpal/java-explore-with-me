package ru.practicum.ewmmain.service.any;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.controller.any.EventSortType;
import ru.practicum.ewmmain.controller.any.EventsRequestParameters;
import ru.practicum.ewmmain.dto.CategoryDto;
import ru.practicum.ewmmain.dto.CompilationDto;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.dto.incoming.ViewStats;
import ru.practicum.ewmmain.dto.mapper.EntityMapper;
import ru.practicum.ewmmain.model.EndpointHitDto;
import ru.practicum.ewmmain.model.Event;
import ru.practicum.ewmmain.model.EventState;
import ru.practicum.ewmmain.model.QEvent;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.statclient.StatClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j //TODO Логирование!
@Service
@RequiredArgsConstructor
public class AnyAccessServiceImpl implements AnyAccessService {
    private final EventRepository eventRepository;
    private final StatClient statClient;

    @Override
    public Collection<EventShortDto> getEvents(EventsRequestParameters parameters, String ip,
                                               String path, String appName) {
        final EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(path)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        statClient.save(endpointHitDto);
        final QEvent event = QEvent.event;

        final Predicate eventDatePredicate = parameters.getRangeStart() == null && parameters.getRangeEnd() == null
                ? event.eventDate.after(LocalDateTime.now())
                : event.eventDate.between(parameters.getRangeStart(), parameters.getRangeEnd());

        final Predicate predicate = (event.annotation.containsIgnoreCase(parameters.getText())
                .or(event.annotation.containsIgnoreCase(parameters.getText())))
                .and(event.category.id.in(parameters.getCategoryIds()))
                .and(event.paid.eq(parameters.getPaid()))
                .and(event.state.eq(EventState.PUBLISHED))
                .and(eventDatePredicate)
                .and(event.confirmedRequests.lt(event.participantLimit));

        Pageable pageable;
        Collection<EventShortDto> eventsResult;

        if (parameters.getSort().equals(EventSortType.EVENT_DATE)) {
            pageable = PageRequest.of(parameters.getFrom() / parameters.getSize(),
                    parameters.getSize(), Sort.by("eventDate"));
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
        Collection<ViewStats> stats = statClient.getStats(LocalDateTime.now().minusYears(1), LocalDateTime.now(),
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
        final EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(path)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        statClient.save(endpointHitDto);
        return eventFullDto;
    }

    @Override
    public Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        // TODO:
        // - формирование параметров
        // - запрос к базе по параметрам
        return null;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        // TODO:
        // - запрос к базе по id
        return null;
    }

    @Override
    public Collection<CategoryDto> getCategories(Integer from, Integer size) {
        // TODO:
        // - формирование параметров
        // - запрос к базе по параметрам
        return null;
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        // TODO:
        // - запрос к базе по id
        return null;
    }

    private Collection<EventShortDto> getEventShorts(EventsRequestParameters parameters,
                                                     Predicate predicate, Pageable pageable) {
        Page<Event> events = eventRepository.findAll(predicate, pageable);
        Set<String> uris = events.stream()
                .map(e -> ("/events/" + e.getId()))
                .collect(Collectors.toSet());
        Collection<ViewStats> viewStats = statClient.getStats(parameters.getRangeStart(),
                parameters.getRangeEnd(), uris, false);
        Collection<EventShortDto> eventsResult = events.stream()
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