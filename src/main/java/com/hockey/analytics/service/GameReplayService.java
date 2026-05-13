package com.hockey.analytics.service;

import com.hockey.analytics.model.Game;
import com.hockey.analytics.model.GameEvent;
import com.hockey.analytics.repository.GameEventRepository;
import com.hockey.analytics.repository.GameRepository;
import com.hockey.analytics.repository.PlayerGameStatsRepository;
import com.hockey.analytics.repository.TeamGameStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GameReplayService {

    private final GameEventRepository gameEventRepository;
    private final TeamGameStatsRepository teamGameStatsRepository;
    private final GameRepository gameRepository;
    private final GameEventProcessingService gameEventProcessingService;
    private final PlayerGameStatsRepository playerGameStatsRepository;

    public GameReplayService(
            GameEventRepository gameEventRepository,
            TeamGameStatsRepository teamGameStatsRepository,
            GameRepository gameRepository,
            GameEventProcessingService gameEventProcessingService, PlayerGameStatsRepository playerGameStatsRepository
    ) {
        this.gameEventRepository = gameEventRepository;
        this.teamGameStatsRepository = teamGameStatsRepository;
        this.gameRepository = gameRepository;
        this.gameEventProcessingService = gameEventProcessingService;
        this.playerGameStatsRepository = playerGameStatsRepository;
    }

    @Transactional
    public void replayGame(UUID gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        List<GameEvent> events =
                gameEventRepository.findByGameIdOrderBySequenceNumber(gameId);

        teamGameStatsRepository.deleteByGame(game);
        teamGameStatsRepository.flush();

        playerGameStatsRepository.deleteByGame(game);
        playerGameStatsRepository.flush();

        for (GameEvent event : events) {
            gameEventProcessingService.applyEventToTeamStats(event);
        }
    }
}