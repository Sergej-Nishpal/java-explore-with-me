package ru.practicum.ewmmain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserShortDto {
    private Long id;
    private String name;
}
