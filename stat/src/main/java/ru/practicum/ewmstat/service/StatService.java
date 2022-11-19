package ru.practicum.ewmstat.service;

import ru.practicum.ewmstat.model.EndpointHit;

public interface StatService {
    void postHit(EndpointHit endpointHit);
}