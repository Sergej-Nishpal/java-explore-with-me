package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class NewCategoryDto {

    @NotBlank
    String name;
}