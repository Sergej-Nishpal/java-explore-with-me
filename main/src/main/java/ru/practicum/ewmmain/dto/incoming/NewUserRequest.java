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

    @Email
    @NotBlank
    String email;

    @NotBlank
    String name;
}