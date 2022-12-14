package ru.practicum.ewmmain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewmmain.model.Category;
import ru.practicum.ewmmain.model.Event;
import ru.practicum.ewmmain.model.EventState;

import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>,
        QuerydslPredicateExecutor<Event> {
    Event findAllByIdAndInitiatorId(long eventId, long initiatorId);

    List<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

    List<Event> findByLocationIdAndState(long locId, EventState eventState);

    List<Event> findByLocationId(long locId);

    Event findFirstByCategoryInOrderByCreatedOn(Set<Category> categories);
}