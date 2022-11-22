package ru.practicum.ewmmain.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class CompilationDto {
    Long id;
    Set<EventShortDto> eventSet;
    Boolean pinned;
    String title;
}