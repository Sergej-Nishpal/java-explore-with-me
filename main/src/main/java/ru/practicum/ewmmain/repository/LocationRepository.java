package ru.practicum.ewmmain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByDescription(String description);
}