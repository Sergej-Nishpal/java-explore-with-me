package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmmain.validation.Marker;
import ru.practicum.ewmmain.model.LocationType;

import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class LocationDto {

    @NotNull(groups = Marker.AdminLocation.class)
    LocationType type;

    @NotNull(groups = Marker.AdminLocation.class)
    String description;

    @NotNull
    Float lat;

    @NotNull
    Float lon;
}