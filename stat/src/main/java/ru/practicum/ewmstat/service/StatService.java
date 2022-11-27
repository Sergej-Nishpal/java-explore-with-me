package ru.practicum.ewmstat.service;

import ru.practicum.ewmstat.model.EndpointHit;
import ru.practicum.ewmstat.model.ViewStats;

import java.util.Collection;
import java.util.Set;

public interface StatService {
    void postHit(EndpointHit endpointHit);

    Collection<ViewStats> getViewStats(String start, String end, Set<String> uris, Boolean unique);
}