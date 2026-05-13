package com.hockey.analytics.repository;

import com.hockey.analytics.model.Game;
import com.hockey.analytics.model.Player;
import com.hockey.analytics.model.PlayerGameStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerGameStatsRepository extends JpaRepository<PlayerGameStats, UUID> {

    Optional<PlayerGameStats> findByGameAndPlayer(Game game, Player player);

    void deleteByGame(Game game);

    List<PlayerGameStats> findByGame(Game game);
}