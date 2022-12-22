package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@Builder
@Jacksonized
public class NewUserRequest {

    @NotBlank
    String name;

    @Email
    @NotBlank
    String email;

    Float lat;

    Float lon;
}