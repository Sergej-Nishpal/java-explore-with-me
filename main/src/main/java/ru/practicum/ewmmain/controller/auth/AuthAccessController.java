package ru.practicum.ewmmain.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.dto.EventFullDto;
import ru.practicum.ewmmain.dto.EventShortDto;
import ru.practicum.ewmmain.dto.ParticipationRequestDto;
import ru.practicum.ewmmain.dto.incoming.NewEventDto;
import ru.practicum.ewmmain.dto.incoming.UpdateEventRequest;
import ru.practicum.ewmmain.service.auth.AuthAccessService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class AuthAccessController {
    private final AuthAccessService authAccessService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsCreatedByUserId(@PathVariable @Positive Long userId,
                                                        @RequestParam(required = false, defaultValue = "0")
                                                        @PositiveOrZero Integer from,
                                                        @RequestParam(required = false, defaultValue = "10")
                                                        @Positive Integer size) {
        log.debug("Получение событий, добавленных текущим пользователем с id = {}.", userId);
        return authAccessService.getEventsCreatedByUserId(userId, from, size);
    }

    @GetMapping("/{userId}/events/near")
    public List<EventShortDto> getEventsNearMe(@PathVariable @Positive Long userId,
                                               @RequestParam(name = "radius") Float radiusKm,
                                               @RequestParam(required = false, defaultValue = "0")
                                               @PositiveOrZero Integer from,
                                               @RequestParam(required = false, defaultValue = "10")
                                               @Positive Integer size) {
        log.debug("Получение событий рядом с пользователем с id = {}, радиус в км. = {}.", userId, radiusKm);
        return authAccessService.getEventsNearMe(userId, radiusKm, from, size);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto updateEventByUserId(@PathVariable @Positive Long userId,
                                            @RequestBody @Valid UpdateEventRequest updateEventRequest) {
        log.debug("Обновление события пользователем с id = {}.", userId);
        return authAccessService.updateEventByUserId(userId, updateEventRequest);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto addEventByUserId(@PathVariable @Positive Long userId,
                                         @RequestBody @Valid NewEventDto newEventDto) {
        log.debug("Добавление события пользователем с id = {}.", userId);
        return authAccessService.addEventByUserId(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUserIdAndEventId(@PathVariable @Positive Long userId,
                                                   @PathVariable @Positive Long eventId) {
        log.debug("Получение события с id = {} пользователя с id = {}.", eventId, userId);
        return authAccessService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto cancelEventByUserIdAndEventId(@PathVariable @Positive Long userId,
                                                      @PathVariable @Positive Long eventId) {
        log.debug("Отмена события с id = {} пользователя с id = {}.", eventId, userId);
        return authAccessService.cancelEventByUserIdAndEventId(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> getParticipationRequestsOfEventId(@PathVariable @Positive Long userId,
                                                                    @PathVariable @Positive Long eventId) {
        log.debug("Получение запросов на участие в событии с id = {} пользователя с id = {}.", eventId, userId);
        return authAccessService.getParticipationRequestsOfEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmParticipationRequestOfEventId(@PathVariable @Positive Long userId,
                                                                        @PathVariable @Positive Long eventId,
                                                                        @PathVariable @Positive Long reqId) {
        log.debug("Подтверждение запроса на участие с id = {} " +
                "в событии с id = {} пользователя с id = {}.", reqId, eventId, userId);
        return authAccessService.confirmParticipationRequestOfEventId(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectParticipationRequestOfEventId(@PathVariable @Positive Long userId,
                                                                       @PathVariable @Positive Long eventId,
                                                                       @PathVariable @Positive Long reqId) {
        log.debug("Отклонение запроса на участие с id = {} " +
                "в событии с id = {} пользователя с id = {}.", reqId, eventId, userId);
        return authAccessService.rejectParticipationRequestOfEventId(userId, eventId, reqId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsOfUserId(@PathVariable @Positive Long userId) {
        log.debug("Получение запросов на участие в событиях пользователя с id = {}.", userId);
        return authAccessService.getParticipationRequestsOfUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto addParticipationRequestsOfUserId(@PathVariable @Positive Long userId,
                                                                    @RequestParam Long eventId) {
        log.debug("Добавление пользователем с id = {} запроса на участие в событии с id = {}.", userId, eventId);
        return authAccessService.addParticipationRequestsOfUserId(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequestId(@PathVariable @Positive Long userId,
                                                                @PathVariable @Positive Long requestId) {
        log.debug("Отмена пользователем с id = {} своего запроса на участие с id = {}.", userId, requestId);
        return authAccessService.cancelParticipationRequestId(userId, requestId);
    }
}