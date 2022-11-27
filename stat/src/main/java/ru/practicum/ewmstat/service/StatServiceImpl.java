package ru.practicum.ewmstat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewmstat.model.EndpointHit;
import ru.practicum.ewmstat.model.ViewStats;
import ru.practicum.ewmstat.repository.StatRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Slf4j //TODO Логирование!
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    public void postHit(EndpointHit endpointHit) {
        statRepository.save(endpointHit);
    }

    @Override
    public Collection<ViewStats> getViewStats(String start, String end, Set<String> uris, Boolean unique) {

        return Collections.emptyList();
    }
}