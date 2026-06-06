package com.hockey.analytics.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "players")
public class Player {

    @Id
    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;


    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private PlayerPosition position;

    @Column(name = "scoring_weight", nullable = false)
    private Integer scoringWeight;

    public Player() {
    }

    public Player(UUID playerId, String name, Team team) {
        this.playerId = playerId;
        this.name = name;
        this.team = team;
    }
}