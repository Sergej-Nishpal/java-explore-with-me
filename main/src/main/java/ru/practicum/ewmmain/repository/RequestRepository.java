package ru.practicum.ewmmain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.model.ParticipationRequest;
import ru.practicum.ewmmain.model.ParticipationRequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEventId(long eventId);

    List<ParticipationRequest> findAllByRequesterId(long requesterId);

    List<ParticipationRequest> findAllByEventIdAndStatus(long eventId, ParticipationRequestStatus status);
}