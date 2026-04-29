package com.hockey.analytics.repository;

import com.hockey.analytics.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
    List<Player> findByTeam_TeamId(UUID teamId);
}