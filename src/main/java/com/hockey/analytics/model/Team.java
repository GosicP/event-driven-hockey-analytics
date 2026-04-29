package com.hockey.analytics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "name", nullable = false)
    private String name;

    public Team() {
    }

    public Team(UUID teamId, String name) {
        this.teamId = teamId;
        this.name = name;
    }
}