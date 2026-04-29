package com.hockey.analytics.repository;

import com.hockey.analytics.model.Game;
import com.hockey.analytics.model.TeamGameStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamGameStatsRepository extends JpaRepository<TeamGameStats, UUID> {
    void deleteByGame(Game game);

    List<TeamGameStats> findByGame(Game game);
}