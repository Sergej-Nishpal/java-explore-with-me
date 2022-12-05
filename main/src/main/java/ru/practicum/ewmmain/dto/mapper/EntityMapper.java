package ru.practicum.ewmmain.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewmmain.dto.*;
import ru.practicum.ewmmain.dto.incoming.*;
import ru.practicum.ewmmain.model.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityMapper {

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(toUserShortDto(event.getInitiator()))
                .views(0L)
                .build();
    }

    public static EventShortDto toEventShortDtoFromLoc(EventLocDto eventLocDto) {
        return EventShortDto.builder()
                .id(eventLocDto.getId())
                .title(eventLocDto.getTitle())
                .annotation(eventLocDto.getAnnotation())
                .category(EntityMapper.toCategoryDto(eventLocDto.getCategory()))
                .paid(eventLocDto.getPaid())
                .eventDate(eventLocDto.getEventDate())
                .confirmedRequests(eventLocDto.getConfirmedRequests())
                .initiator(EntityMapper.toUserShortDto(eventLocDto.getInitiator()))
                .distanceKilometer(eventLocDto.getDistanceKm())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .initiator(EntityMapper.toUserShortDto(event.getInitiator()))
                .requestModeration(event.getRequestModeration())
                .createdOn(event.getCreatedOn())
                .state(event.getState())
                .publishedOn(event.getPublishedOn())
                .confirmedRequests(event.getConfirmedRequests())
                .views(0L)
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, Location location, User initiator) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .category(category)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .confirmedRequests(0)
                .location(location)
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .requestModeration(newEventDto.getRequestModeration())
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static User toUser(NewUserRequest newUserRequest, Location location) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .locationId(location != null ? location.getId() : null)
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .locationId(user.getLocationId())
                .build();
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(EntityMapper::toEventShortDto)
                        .collect(Collectors.toSet()))
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .build();
    }

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .type(locationDto.getType())
                .description(locationDto.getDescription())
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static LocationFullDto toLocationFullDto(Location location) {
        return LocationFullDto.builder()
                .type(location.getType())
                .description(location.getDescription())
                .lat(location.getLat())
                .lon(location.getLon())
                .createdOn(location.getCreatedOn())
                .build();
    }

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .event(participationRequest.getEventId())
                .requester(participationRequest.getRequesterId())
                .status(participationRequest.getStatus())
                .build();
    }
}