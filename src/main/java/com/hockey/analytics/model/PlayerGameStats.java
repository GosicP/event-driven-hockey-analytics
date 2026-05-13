package com.hockey.analytics.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "player_game_stats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_player_game_stats_game_player", columnNames = {"game_id", "player_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerGameStats {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "shots", nullable = false)
    private Integer shots;

    @Column(name = "goals", nullable = false)
    private Integer goals;

    @Column(name = "penalties", nullable = false)
    private Integer penalties;
}