package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Value
@Builder
@Jacksonized
public class NewCategoryDto {

    @NotBlank
    String name;
}