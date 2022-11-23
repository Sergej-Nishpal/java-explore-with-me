package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Value
@Builder
public class NewCompilationDto {
    Set<Long> events;
    Boolean pinned;

    @NotBlank
    String title;
}