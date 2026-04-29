package com.hockey.analytics.service;

import com.hockey.analytics.model.*;
import com.hockey.analytics.repository.GameEventRepository;
import com.hockey.analytics.repository.GameRepository;
import com.hockey.analytics.repository.TeamGameStatsRepository;
import com.hockey.analytics.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GameReplayService {
    private final GameEventRepository gameEventRepository;
    private final TeamGameStatsRepository teamGameStatsRepository;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    public GameReplayService(
            GameEventRepository gameEventRepository,
            TeamGameStatsRepository teamGameStatsRepository,
            GameRepository gameRepository, TeamRepository teamRepository) {
        this.gameEventRepository = gameEventRepository;
        this.teamGameStatsRepository = teamGameStatsRepository;
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional
    public void replayGame(UUID gameId) {
        List<GameEvent> events =
                gameEventRepository.findByGameIdOrderBySequenceNumber(gameId);

        Game game = gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        teamGameStatsRepository.deleteByGame(game);
        teamGameStatsRepository.flush();

        Map<UUID, TeamGameStats> statsByTeamId = new HashMap<>();

        for (GameEvent event : events) {
            UUID teamId = event.getTeamId();

            if (teamId == null) {
                continue;
            }

            TeamGameStats stats = statsByTeamId.computeIfAbsent(
                    teamId,
                    currentTeamId -> {
                        Team team = teamRepository.findById(currentTeamId)
                                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + currentTeamId));

                        return TeamGameStats.builder()
                                .id(UUID.randomUUID())
                                .game(game)
                                .team(team)
                                .goals(0)
                                .shots(0)
                                .penalties(0)
                                .build();
                    }
            );

            applyEventToStats(event, stats);
        }

        teamGameStatsRepository.saveAll(statsByTeamId.values());
    }

    private void applyEventToStats(GameEvent event, TeamGameStats stats) {
        EventType eventType = event.getEventType();

        if (eventType == null) {
            return;
        }

        switch (eventType) {
            case GOAL -> {
                stats.setGoals(stats.getGoals() + 1);
                stats.setShots(stats.getShots() + 1);
            }
            case SHOT -> stats.setShots(stats.getShots() + 1);
            case PENALTY -> stats.setPenalties(stats.getPenalties() + 1);
            default -> {
                // Events like PERIOD_START, PERIOD_END, GAME_END do not affect team stats.
            }
        }
    }

}
