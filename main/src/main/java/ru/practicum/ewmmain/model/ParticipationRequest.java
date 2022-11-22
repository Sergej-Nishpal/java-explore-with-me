package ru.practicum.ewmmain.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_on")
    private String created;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "requester_id")
    private String requesterId;

    @Enumerated(EnumType.STRING)
    private ParticipationRequestStatus status;
}