package com.hockey.analytics.service;

import com.hockey.analytics.model.*;
import com.hockey.analytics.repository.GameEventRepository;
import com.hockey.analytics.repository.GameRepository;
import com.hockey.analytics.repository.TeamGameStatsRepository;
import com.hockey.analytics.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hockey.analytics.repository.PlayerGameStatsRepository;
import com.hockey.analytics.repository.PlayerRepository;

import java.util.UUID;

@Service
public class GameEventProcessingService {
    private final GameEventRepository gameEventRepository;
    private final TeamGameStatsRepository teamGameStatsRepository;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final PlayerGameStatsRepository playerGameStatsRepository;
    private final PlayerRepository playerRepository;

    public GameEventProcessingService(GameEventRepository gameEventRepository,
                                      TeamGameStatsRepository teamGameStatsRepository,
                                      GameRepository gameRepository,
                                      TeamRepository teamRepository, PlayerGameStatsRepository playerGameStatsRepository, PlayerRepository playerRepository) {
        this.gameEventRepository = gameEventRepository;
        this.teamGameStatsRepository = teamGameStatsRepository;
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
        this.playerGameStatsRepository = playerGameStatsRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public void processNewEvent(GameEvent event) {
        gameEventRepository.save(event);
        applyEventToTeamStats(event);
        applyEventToPlayerStats(event);
    }

    @Transactional
    public void applyEventToTeamStats(GameEvent event) {
        if (event.getTeamId() == null) {
            return;
        }

        Game game = gameRepository.findById(event.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + event.getGameId()));

        Team team = teamRepository.findById(event.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + event.getTeamId()));

        TeamGameStats stats = teamGameStatsRepository
                .findByGameAndTeam(game, team)
                .orElseGet(() -> TeamGameStats.builder()
                        .id(UUID.randomUUID())
                        .game(game)
                        .team(team)
                        .shots(0)
                        .goals(0)
                        .penalties(0)
                        .build());

        switch (event.getEventType()) {
            case SHOT -> stats.setShots(stats.getShots() + 1);
            case GOAL -> {
                stats.setGoals(stats.getGoals() + 1);
                stats.setShots(stats.getShots() + 1);
            }
            case PENALTY -> stats.setPenalties(stats.getPenalties() + 1);
            default -> {
            }
        }

        teamGameStatsRepository.save(stats);
    }

    private void applyEventToPlayerStats(GameEvent event) {

        if (event.getPlayerId() == null || event.getTeamId() == null) {
            return;
        }

        Game game = gameRepository.findById(event.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + event.getGameId()));

        Team team = teamRepository.findById(event.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + event.getTeamId()));

        Player player = playerRepository.findById(event.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + event.getPlayerId()));

        PlayerGameStats stats = playerGameStatsRepository
                .findByGameAndPlayer(game, player)
                .orElseGet(() -> PlayerGameStats.builder()
                        .id(UUID.randomUUID())
                        .game(game)
                        .player(player)
                        .team(team)
                        .shots(0)
                        .goals(0)
                        .penalties(0)
                        .build());

        switch (event.getEventType()) {
            case SHOT -> stats.setShots(stats.getShots() + 1);

            case GOAL -> {
                stats.setGoals(stats.getGoals() + 1);
                stats.setShots(stats.getShots() + 1);
            }

            case PENALTY -> stats.setPenalties(stats.getPenalties() + 1);

            default -> {
            }
        }

        playerGameStatsRepository.save(stats);
    }
}
