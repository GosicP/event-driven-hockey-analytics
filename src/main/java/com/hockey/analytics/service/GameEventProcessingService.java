package com.hockey.analytics.service;

import com.hockey.analytics.model.Game;
import com.hockey.analytics.model.GameEvent;
import com.hockey.analytics.model.Team;
import com.hockey.analytics.model.TeamGameStats;
import com.hockey.analytics.repository.GameEventRepository;
import com.hockey.analytics.repository.GameRepository;
import com.hockey.analytics.repository.TeamGameStatsRepository;
import com.hockey.analytics.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GameEventProcessingService {
    private final GameEventRepository gameEventRepository;
    private final TeamGameStatsRepository teamGameStatsRepository;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    public GameEventProcessingService(GameEventRepository gameEventRepository,
                                      TeamGameStatsRepository teamGameStatsRepository,
                                      GameRepository gameRepository,
                                      TeamRepository teamRepository) {
        this.gameEventRepository = gameEventRepository;
        this.teamGameStatsRepository = teamGameStatsRepository;
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public void processEvent(GameEvent event) {
        gameEventRepository.save(event);

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
}
