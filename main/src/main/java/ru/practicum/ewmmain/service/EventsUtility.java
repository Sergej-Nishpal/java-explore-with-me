package ru.practicum.ewmmain.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.ewmmain.dto.EndpointHitDto;
import ru.practicum.ewmmain.dto.EventLocDto;
import ru.practicum.ewmmain.dto.EventNotificationDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.dto.incoming.ViewStats;
import ru.practicum.ewmmain.dto.mapper.EntityMapper;
import ru.practicum.ewmmain.model.Event;
import ru.practicum.ewmmain.model.QEvent;
import ru.practicum.ewmmain.model.QLocation;
import ru.practicum.ewmmain.model.QUser;
import ru.practicum.ewmmain.repository.CategoryRepository;
import ru.practicum.ewmmain.repository.EventRepository;
import ru.practicum.ewmmain.statclient.StatClient;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventsUtility {
    private static final String EVENTS_PATH = "events";

    private final EntityManager entityManager;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatClient statClient;

    public List<EventShortDto> getNearEvents(float lat, float lon, float radiusKm, Pageable pageable) {
        final JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        final QEvent event = QEvent.event;
        final NumberExpression<Float> distanceKilometer = Expressions
                .numberTemplate(Float.class,
                        "distance({0}, {1}, {2}, {3})",
                        lat,
                        lon,
                        event.location.lat,
                        event.location.lon);
        final List<EventShortDto> result = queryFactory.select(Projections.constructor(EventLocDto.class,
                        event.id,
                        event.title,
                        event.annotation,
                        event.category,
                        event.paid,
                        event.eventDate,
                        event.confirmedRequests,
                        event.initiator,
                        distanceKilometer))
                .from(event)
                .where(Expressions.booleanTemplate("distance({0}, {1}, {2}, {3}) < {4}",
                        lat,
                        lon,
                        event.location.lat,
                        event.location.lon,
                        radiusKm))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(EntityMapper::toEventShortDtoFromLoc)
                .sorted(Comparator.comparing(EventShortDto::getDistanceKilometer))
                .collect(Collectors.toList());
        return addViewsAndSortEventShortDtoList(result);
    }

    public List<EventShortDto> getEventsWithDistance(float lat, float lon, Pageable pageable) {
        final JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        final QEvent event = QEvent.event;
        final NumberExpression<Float> distanceKilometer = Expressions.numberTemplate(Float.class,
                "distance({0}, {1}, {2}, {3})",
                lat,
                lon,
                event.location.lat,
                event.location.lon);
        return queryFactory.select(Projections.constructor(EventLocDto.class,
                        event.id,
                        event.title,
                        event.annotation,
                        event.category,
                        event.paid,
                        event.eventDate,
                        event.confirmedRequests,
                        event.initiator,
                        distanceKilometer))
                .from(event)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(EntityMapper::toEventShortDtoFromLoc)
                .sorted(Comparator.comparing(EventShortDto::getDistanceKilometer))
                .collect(Collectors.toList());
    }

    public List<EventNotificationDto> getNotificationList(Event pubEvent, float minimalDistance) {
        final JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        final QEvent event = QEvent.event;
        final QUser user = QUser.user;
        final QLocation location = QLocation.location;
        final NumberExpression<Float> distanceKilometer = Expressions.numberTemplate(Float.class,
                "distance({0}, {1}, {2}, {3})",
                event.location.lat,
                event.location.lon,
                pubEvent.getLocation().getLat(),
                pubEvent.getLocation().getLon(),
                minimalDistance);
        final List<EventNotificationDto> eventNotificationDtoList = queryFactory
                .select(Projections.constructor(EventNotificationDto.class,
                        user.email,
                        user.name,
                        event.title,
                        location.description,
                        distanceKilometer,
                        event.location.lat,
                        event.location.lon,
                        event.eventDate))
                .from(user)
                .where(user.id.ne(event.initiator.id).and(user.locationId.isNotNull()))
                .join(location)
                .on(user.locationId.eq(location.id))
                .join(event)
                .on(event.id.eq(pubEvent.getId()))
                .where(Expressions.booleanTemplate("distance({0}, {1}, {2}, {3}) <= {4}",
                        location.lat,
                        location.lon,
                        pubEvent.getLocation().getLat(),
                        pubEvent.getLocation().getLon(),
                        minimalDistance))
                .fetch();
        for (EventNotificationDto eventNotificationDto : eventNotificationDtoList) {
            log.debug(eventNotificationDto.toString());
        }
        return eventNotificationDtoList;
    }

    public List<EventShortDto> addViewsAndSortEventShortDtoList(List<EventShortDto> incomingList) {
        final Set<String> uris = incomingList.stream()
                .map(e -> ("/" + EVENTS_PATH + "/" + e.getId()))
                .collect(Collectors.toSet());
        final LocalDateTime startDate = eventRepository
                .findFirstByCategoryInOrderByCreatedOn(new HashSet<>(categoryRepository.findAll()))
                .getCreatedOn();
        final LocalDateTime endDate = LocalDateTime.now();
        final List<ViewStats> viewStats = statClient.getStats(startDate, endDate, uris, false);
        final List<EventShortDto> eventsResultWithViews = checkAndSetViews(incomingList, viewStats);
        eventsResultWithViews.sort(Comparator.comparing(EventShortDto::getDistanceKilometer));
        return eventsResultWithViews;
    }

    public List<EventShortDto> getEventShortsWithViewsSorted(Page<Event> events) {
        Set<String> uris = events.stream()
                .map(e -> ("/" + EVENTS_PATH + "/" + e.getId()))
                .collect(Collectors.toSet());
        final LocalDateTime startDate = eventRepository
                .findFirstByCategoryInOrderByCreatedOn(new HashSet<>(categoryRepository.findAll()))
                .getCreatedOn();
        final LocalDateTime endDate = LocalDateTime.now();
        final List<EventShortDto> eventsResult = events.stream()
                .map(EntityMapper::toEventShortDto)
                .collect(Collectors.toList());
        final List<ViewStats> viewStats = statClient.getStats(startDate, endDate, uris, false);
        final List<EventShortDto> eventsResultWithViews = checkAndSetViews(eventsResult, viewStats);
        eventsResultWithViews.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        return eventsResultWithViews;
    }

    public List<EventShortDto> checkAndSetViews(List<EventShortDto> eventsResult, List<ViewStats> viewStats) {
        for (EventShortDto eventShortDto : eventsResult) {
            if (!viewStats.isEmpty()) {
                for (ViewStats views : viewStats) {
                    if (views.getUri().contains(eventShortDto.getId().toString())) {
                        eventShortDto.setViews(views.getHits());
                    } else {
                        eventShortDto.setViews(0L);
                    }
                }
            } else {
                eventShortDto.setViews(0L);
            }
        }
        return eventsResult;
    }

    public void saveHitOfViewedEvent(long eventId, String appName, String ip) {
        final EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(appName)
                .uri("/" + EVENTS_PATH + "/" + eventId)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        statClient.save(endpointHitDto);
    }
}