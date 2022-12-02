package ru.practicum.ewmstat.service;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmstat.model.EndpointHit;
import ru.practicum.ewmstat.model.QEndpointHit;
import ru.practicum.ewmstat.model.ViewStats;
import ru.practicum.ewmstat.repository.StatRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void postHit(EndpointHit endpointHit) {
        statRepository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end,
                                        Set<String> uris, Boolean unique) {
        final QEndpointHit endpointHit = QEndpointHit.endpointHit;
        final JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        final Predicate predicate = endpointHit.timestamp.between(start, end)
                .and(endpointHit.uri.in(uris));
        final NumberPath<Long> views = Expressions.numberPath(Long.class, "hits");
        final NumberExpression<Long> ipCount = Boolean.TRUE.equals(unique)
                ? endpointHit.ip.countDistinct().as(views)
                : endpointHit.ip.count().as(views);
        return queryFactory.select(Projections.constructor(ViewStats.class, endpointHit.app, endpointHit.uri, ipCount))
                .from(endpointHit)
                .where(predicate)
                .groupBy(endpointHit.app)
                .groupBy(endpointHit.uri)
                .fetch();
    }
}