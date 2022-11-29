package ru.practicum.ewmmain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmmain.model.Event;
import ru.practicum.ewmmain.model.EventState;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>,
        QuerydslPredicateExecutor<Event> {
    Event findAllByIdAndInitiatorId(long eventId, long initiatorId);

    List<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

    Event findByIdAndState(long eventId, EventState eventState);
}