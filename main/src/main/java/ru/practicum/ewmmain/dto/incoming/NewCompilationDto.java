package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Value
@Builder
@Jacksonized
public class NewCompilationDto {

    @NotBlank
    String title;
    Set<Long> events;
    Boolean pinned;
}