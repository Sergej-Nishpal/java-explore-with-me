package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class GeoData {
    Float lat;
    Float lon;
    Float radiusKm;
}