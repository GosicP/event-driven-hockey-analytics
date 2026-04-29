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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GameStatsService {

    private final GameEventRepository gameEventRepository;
    private final TeamGameStatsRepository statsRepository;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    public GameStatsService(GameEventRepository gameEventRepository,
                            TeamGameStatsRepository statsRepository,
                            GameRepository gameRepository,
                            TeamRepository teamRepository) {
        this.gameEventRepository = gameEventRepository;
        this.statsRepository = statsRepository;
        this.gameRepository = gameRepository;
        this.teamRepository = teamRepository;
    }

    public void calculateStats(UUID gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        List<GameEvent> events = gameEventRepository.findByGameIdOrderBySequenceNumber(gameId);

        Map<UUID, TeamGameStats> statsMap = new HashMap<>();

        for (GameEvent event : events) {
            UUID teamId = event.getTeamId();
            if (teamId == null) {
                continue;
            }

            statsMap.putIfAbsent(teamId, createEmptyStats(game, teamId));

            TeamGameStats stats = statsMap.get(teamId);

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
        }

        statsRepository.saveAll(statsMap.values());
    }

    private TeamGameStats createEmptyStats(Game game, UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));

        TeamGameStats stats = new TeamGameStats();
        stats.setId(UUID.randomUUID());
        stats.setGame(game);
        stats.setTeam(team);
        stats.setShots(0);
        stats.setGoals(0);
        stats.setPenalties(0);
        return stats;
    }
}