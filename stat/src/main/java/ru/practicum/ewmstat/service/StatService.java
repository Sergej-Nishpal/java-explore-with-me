package ru.practicum.ewmstat.service;

import ru.practicum.ewmstat.model.EndpointHit;
import ru.practicum.ewmstat.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface StatService {
    void postHit(EndpointHit endpointHit);

    List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end, Set<String> uris, Boolean unique);
}