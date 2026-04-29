package com.hockey.analytics.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "game_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameEvent {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "event_time", nullable = false)
    private Instant eventTime;

    @Column(name = "period_number")
    private Integer periodNumber;

    @Column(name = "team_id")
    private UUID teamId;

    @Column(name = "player_id")
    private UUID playerId;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

}