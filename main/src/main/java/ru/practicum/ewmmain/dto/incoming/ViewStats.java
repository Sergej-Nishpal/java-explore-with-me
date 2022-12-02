package ru.practicum.ewmmain.dto.incoming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@AllArgsConstructor
public class ViewStats {
    String app;
    String uri;
    Long hits;
}