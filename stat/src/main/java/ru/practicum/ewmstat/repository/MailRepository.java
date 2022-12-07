package ru.practicum.ewmstat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmstat.model.EndpointHit;
import ru.practicum.ewmstat.model.EventNotification;

public interface MailRepository extends JpaRepository<EventNotification, Long>,
        QuerydslPredicateExecutor<EndpointHit> {
}