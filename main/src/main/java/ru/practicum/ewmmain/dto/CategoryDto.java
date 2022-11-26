package ru.practicum.ewmmain.dto;

import lombok.*;

@Value
@Builder
public class CategoryDto {
    Long id;
    String name;
}