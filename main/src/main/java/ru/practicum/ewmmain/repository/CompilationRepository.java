package ru.practicum.ewmmain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}