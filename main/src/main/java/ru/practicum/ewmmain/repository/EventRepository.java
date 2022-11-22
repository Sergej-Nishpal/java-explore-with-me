package ru.practicum.ewmmain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmmain.model.Event;

import java.util.Collection;

public interface EventRepository extends JpaRepository<Event, Long> {

    //@Query
    //Collection<Event> findAllWithParameters();
}