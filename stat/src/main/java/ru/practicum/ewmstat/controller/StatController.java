package ru.practicum.ewmstat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmstat.dto.mapper.EndpointHitDto;
import ru.practicum.ewmstat.dto.mapper.EndpointHitMapper;
import ru.practicum.ewmstat.service.StatService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    void postHit(EndpointHitDto endpointHitDto) {
        log.debug("");
        statService.postHit(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }
}