package ru.practicum.ewmstat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmstat.model.EndpointHit;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {

}