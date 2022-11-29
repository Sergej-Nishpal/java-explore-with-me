package ru.practicum.ewmstat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmstat.dto.EndpointHitDto;
import ru.practicum.ewmstat.dto.mapper.EndpointHitMapper;
import ru.practicum.ewmstat.model.EndpointHit;
import ru.practicum.ewmstat.model.ViewStats;
import ru.practicum.ewmstat.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
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
        statService.postHit(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @GetMapping("/stats")
    public Collection<ViewStats> getViewStats(@RequestParam
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                              @RequestParam
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                              @RequestParam Set<String> uris,
                                              @RequestParam(defaultValue = "false") Boolean unique) {
        log.debug("Получение статистики по посещениям.");
        return statService.getViewStats(start, end, uris, unique);
    }
}