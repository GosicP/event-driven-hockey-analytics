package com.hockey.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "team_game_stats",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_team_game_stats_game_team", columnNames = {"game_id", "team_id"})
        }
)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class TeamGameStats {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "shots", nullable = false)
    private Integer shots;

    @Column(name = "goals", nullable = false)
    private Integer goals;

    @Column(name = "penalties", nullable = false)
    private Integer penalties;

    public TeamGameStats() {

    }
}