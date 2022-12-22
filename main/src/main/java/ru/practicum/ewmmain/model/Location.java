package ru.practicum.ewmmain.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "locations", schema = "public")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private LocationType type;

    @Column(name = "description")
    private String description;

    @Column(name = "latitude")
    private float lat;

    @Column(name = "longitude")
    private float lon;

    @Column(name = "created_on")
    private LocalDateTime createdOn;
}