package com.hockey.analytics.service;

import com.hockey.analytics.dto.GameStatsResponse;
import com.hockey.analytics.dto.PlayerStatsResponse;
import com.hockey.analytics.dto.TeamStatsResponse;
import com.hockey.analytics.model.Game;
import com.hockey.analytics.repository.GameRepository;
import com.hockey.analytics.repository.PlayerGameStatsRepository;
import com.hockey.analytics.repository.TeamGameStatsRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.UUID;

@Service
public class GameStatsQueryService {

    private final GameRepository gameRepository;
    private final TeamGameStatsRepository teamGameStatsRepository;
    private final PlayerGameStatsRepository playerGameStatsRepository;

    public GameStatsQueryService(GameRepository gameRepository,
                                 TeamGameStatsRepository teamGameStatsRepository,
                                 PlayerGameStatsRepository playerGameStatsRepository) {
        this.gameRepository = gameRepository;
        this.teamGameStatsRepository = teamGameStatsRepository;
        this.playerGameStatsRepository = playerGameStatsRepository;
    }

    public GameStatsResponse getGameStats(UUID gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        var teamStats = teamGameStatsRepository.findByGame(game).stream()
                .map(stats -> new TeamStatsResponse(
                        stats.getTeam().getTeamId(),
                        stats.getTeam().getName(),
                        stats.getShots(),
                        stats.getGoals(),
                        stats.getPenalties()
                ))
                .toList();

        var playerStats = playerGameStatsRepository.findByGame(game).stream()
                .map(stats -> new PlayerStatsResponse(
                        stats.getPlayer().getPlayerId(),
                        stats.getPlayer().getName(),
                        stats.getTeam().getName(),
                        stats.getPlayer().getPosition().name(),
                        stats.getShots(),
                        stats.getGoals(),
                        stats.getPenalties()
                ))
                .sorted(Comparator
                        .comparing(PlayerStatsResponse::goals).reversed()
                        .thenComparing(PlayerStatsResponse::shots, Comparator.reverseOrder()))
                .toList();

        return new GameStatsResponse(
                game.getGameId(),
                game.getHomeTeam().getName(),
                game.getAwayTeam().getName(),
                teamStats,
                playerStats
        );
    }
}