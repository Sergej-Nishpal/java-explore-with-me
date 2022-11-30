package ru.practicum.ewmmain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.model.ParticipationRequest;
import ru.practicum.ewmmain.model.ParticipationRequestStatus;

import java.util.Collection;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Collection<ParticipationRequest> findAllByEventId(long eventId);

    Collection<ParticipationRequest> findAllByRequesterId(long requesterId);

    Collection<ParticipationRequest> findAllByEventIdAndStatus(long eventId, ParticipationRequestStatus status);
}