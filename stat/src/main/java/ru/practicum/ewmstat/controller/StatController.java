package ru.practicum.ewmstat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmstat.dto.EndpointHitDto;
import ru.practicum.ewmstat.dto.mapper.StatEntityMapper;
import ru.practicum.ewmstat.dto.EventNotificationDto;
import ru.practicum.ewmstat.model.ViewStats;
import ru.practicum.ewmstat.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    public void postHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.debug(String.format("Сохранение информации о запросе пользователя на uri: %s", endpointHitDto.getUri()));
        statService.postHit(StatEntityMapper.toEndpointHit(endpointHitDto));
    }

    @GetMapping("/stats")
    public List<ViewStats> getViewStats(@RequestParam
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                        @RequestParam
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                        @RequestParam Set<String> uris,
                                        @RequestParam(defaultValue = "false") Boolean unique) {
        log.debug("Получение статистики по посещениям.");
        return statService.getViewStats(start, end, uris, unique);
    }

    @PostMapping("/mails")
    public void mail(@RequestBody @Valid List<EventNotificationDto> notifications) {
        log.debug(String.format("Сохранение уведомлений для отправки пользователям, %d шт.", notifications.size()));
        statService.postMails(StatEntityMapper.toEventNotifications(notifications));
    }
}