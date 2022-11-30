package ru.practicum.ewmstat.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmstat.model.EndpointHit;
import ru.practicum.ewmstat.model.QEndpointHit;
import ru.practicum.ewmstat.model.ViewStats;
import ru.practicum.ewmstat.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j //TODO Логирование!
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private static final String APP_NAME = "ewm-main-service";

    private final StatRepository statRepository;

    @Override
    @Transactional
    public void postHit(EndpointHit endpointHit) {
        statRepository.save(endpointHit);
    }

    @Override
    public Collection<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end,
                                              Set<String> uris, Boolean unique) {
        final QEndpointHit endpointHit = QEndpointHit.endpointHit;
        final Predicate predicate = endpointHit.timestamp.between(start, end)
                .and(endpointHit.uri.in(uris));
        Iterable<EndpointHit> endpoints = statRepository.findAll(predicate);

        Collection<ViewStats> viewStatsCollection = new ArrayList<>();

        for (String uri : uris) {
            ViewStats viewStats;
            long counter = 0;
            //List<String> apps = new ArrayList<>();
            List<String> ips = new ArrayList<>();
            for (EndpointHit hit : endpoints) {
                if (hit.getUri().equals(uri)) {
                    /*if (!apps.contains(hit.getApp())) {
                        apps.add(hit.getApp());
                    }*/

                    if (!ips.contains(hit.getIp())) {
                        ips.add(hit.getIp());
                    }

                    counter++;
                }
            }
            viewStats = ViewStats.builder()
                    .app(APP_NAME)
                    .uri(uri)
                    .hits(Boolean.TRUE.equals(unique) ? ips.size() : counter)
                    .build();
            viewStatsCollection.add(viewStats);
        }

        return viewStatsCollection;
    }
}