package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewmmain.model.LocationType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class LocationDto {

    LocationType type;

    String description;

    @NotNull
    Float lat;

    @NotNull
    Float lon;
}