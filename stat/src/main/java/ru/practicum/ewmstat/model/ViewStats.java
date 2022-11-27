package ru.practicum.ewmstat.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ViewStats {
    String app;
    String uri;
    Long hits;
}
