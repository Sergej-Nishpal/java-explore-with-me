package ru.practicum.ewmmain.dto;

import lombok.*;

@Value
@Builder
public class UserShortDto {
    Long id;
    String name;
}