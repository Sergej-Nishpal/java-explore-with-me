package ru.practicum.ewmmain.dto.incoming;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@Builder
public class NewUserRequest {

    @NotBlank
    @Email
    String email;

    @NotBlank
    String name;
}