package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Value
@Builder
@Jacksonized
public class LocationDto {

    @NotNull
    Float lat;

    @NotNull
    Float lon;

    @Override
    public boolean equals(Object otherLocation) {
        if (this == otherLocation) return true;
        if (otherLocation == null || getClass() != otherLocation.getClass()) return false;

        LocationDto that = (LocationDto) otherLocation;

        if (!lat.equals(that.lat)) return false;
        return lon.equals(that.lon);
    }

    @Override
    public int hashCode() {
        int result = lat.hashCode();
        result = 31 * result + lon.hashCode();
        return result;
    }
}