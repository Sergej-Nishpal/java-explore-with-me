package ru.practicum.ewmstat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mails", schema = "public")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receiver_email", nullable = false)
    String userEmail;

    @Column(name = "receiver_name", nullable = false)
    String userName;

    @Column(name = "event_title", nullable = false)
    String eventTitle;

    @Column(name = "event_location", nullable = false)
    String locationDescription;

    @Column(name = "event_distance", nullable = false)
    Float eventDistanceKilometer;

    @Column(name = "event_lat", nullable = false)
    Float eventLat;

    @Column(name = "event_lon", nullable = false)
    Float eventLon;

    @Column(name = "event_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @Column(name = "created_timestamp", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @Column(name = "sent_timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime sentAt;
}