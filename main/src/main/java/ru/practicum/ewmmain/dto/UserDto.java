package ru.practicum.ewmmain.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDto {
    Long id;
    String name;
    String email;
    Long locationId;
}