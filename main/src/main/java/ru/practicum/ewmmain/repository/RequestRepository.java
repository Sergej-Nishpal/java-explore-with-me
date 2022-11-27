package ru.practicum.ewmmain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.model.ParticipationRequest;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
}