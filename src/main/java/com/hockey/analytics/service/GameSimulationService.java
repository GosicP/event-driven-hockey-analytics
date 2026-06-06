package com.hockey.analytics.service;

import com.hockey.analytics.exception.GameAlreadySimulatedException;
import com.hockey.analytics.kafka.GameEventProducer;
import com.hockey.analytics.model.*;
import com.hockey.analytics.repository.GameEventRepository;
import com.hockey.analytics.repository.GameRepository;
import com.hockey.analytics.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class GameSimulationService {

    private static final int EVENTS_PER_PERIOD = 22;
    private static final int GOAL_CHANCE_PERCENT = 9;
    private static final int PENALTY_CHANCE_PERCENT = 8;

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameEventProducer gameEventProducer;
    private final GameEventRepository gameEventRepository;
    private final GameEventProcessingService gameEventProcessingService;

    private final Random random = new Random();

    public GameSimulationService(GameRepository gameRepository,
                                 PlayerRepository playerRepository, GameEventProducer gameEventProducer,
                                 GameEventRepository gameEventRepository, GameEventProcessingService gameEventProcessingService) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.gameEventProducer = gameEventProducer;
        this.gameEventRepository = gameEventRepository;
        this.gameEventProcessingService = gameEventProcessingService;
    }

    public void simulateGame(UUID gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found: " + gameId));

        if (gameEventRepository.existsByGameId(gameId)) {
            throw new GameAlreadySimulatedException(gameId);
        }

        List<Player> homePlayers = playerRepository.findByTeam_TeamId(game.getHomeTeam().getTeamId());
        List<Player> awayPlayers = playerRepository.findByTeam_TeamId(game.getAwayTeam().getTeamId());

        List<GameEvent> events = new ArrayList<>();

        int sequence = 1;
        Instant currentTime = game.getGameDate();

        events.add(createEvent(game, EventType.GAME_STARTED, currentTime, null, null, sequence++));

        for (int period = 1; period <= 3; period++) {
            currentTime = currentTime.plus(1, ChronoUnit.MINUTES);
            events.add(createEvent(game, EventType.PERIOD_STARTED, currentTime, period, null, sequence++));

            for (int i = 0; i < EVENTS_PER_PERIOD; i++) {
                boolean homeAttack = random.nextBoolean();

                List<Player> attackingPlayers = homeAttack ? homePlayers : awayPlayers;
                List<Player> defendingPlayers = homeAttack ? awayPlayers : homePlayers;

                Player shooter = randomSkaterForShot(attackingPlayers);

                currentTime = currentTime.plusSeconds(30);

                events.add(createEvent(
                        game,
                        EventType.SHOT,
                        currentTime,
                        period,
                        shooter,
                        sequence++
                ));

                if (isGoal(shooter)) {
                    currentTime = currentTime.plusSeconds(5);
                    events.add(createEvent(
                            game,
                            EventType.GOAL,
                            currentTime,
                            period,
                            shooter,
                            sequence++
                    ));
                }

                if (random.nextInt(100) < PENALTY_CHANCE_PERCENT) {
                    Player penalized = randomSkaterForPenalty(defendingPlayers);

                    currentTime = currentTime.plusSeconds(10);
                    events.add(createEvent(
                            game,
                            EventType.PENALTY,
                            currentTime,
                            period,
                            penalized,
                            sequence++
                    ));
                }
            }

            currentTime = currentTime.plus(1, ChronoUnit.MINUTES);
            events.add(createEvent(game, EventType.PERIOD_ENDED, currentTime, period, null, sequence++));
        }

        currentTime = currentTime.plus(1, ChronoUnit.MINUTES);
        events.add(createEvent(game, EventType.GAME_ENDED, currentTime, null, null, sequence++));

        for (GameEvent event : events) {
            gameEventProducer.send(event);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Simulation interrupted", e);
            }
        }
    }

    private GameEvent createEvent(Game game,
                                  EventType eventType,
                                  Instant eventTime,
                                  Integer period,
                                  Player player,
                                  Integer sequenceNumber) {
        GameEvent event = new GameEvent();
        event.setEventId(UUID.randomUUID());
        event.setGameId(game.getGameId());
        event.setEventType(eventType);
        event.setEventTime(eventTime);
        event.setPeriodNumber(period);
        event.setSequenceNumber(sequenceNumber);

        if (player != null) {
            event.setPlayerId(player.getPlayerId());
            event.setTeamId(player.getTeam().getTeamId());
        }

        return event;
    }

    private Player randomSkaterForShot(List<Player> players) {
        return selectWeightedSkater(players);
    }

    private Player randomSkaterForPenalty(List<Player> players) {
        List<Player> skaters = players.stream()
                .filter(player -> player.getPosition() != PlayerPosition.GOALIE)
                .toList();

        if (skaters.isEmpty()) {
            throw new IllegalStateException("No skaters available for penalty event.");
        }

        return randomPlayer(skaters);
    }

    private boolean isGoal(Player shooter) {
        if (shooter.getPosition() == PlayerPosition.GOALIE) {
            return false;
        }

        int baseChance = switch (shooter.getPosition()) {
            case FORWARD -> GOAL_CHANCE_PERCENT;
            case DEFENSEMAN -> Math.max(1, GOAL_CHANCE_PERCENT - 3);
            case GOALIE -> 0;
        };

        int scoringWeight = shooter.getScoringWeight() != null
                ? shooter.getScoringWeight()
                : 1;

        int bonusChance = Math.max(0, (scoringWeight - 6) / 3);

        int finalChance = Math.min(baseChance + bonusChance, 15);

        return random.nextInt(100) < finalChance;
    }

    private Player randomPlayer(List<Player> players) {
        return players.get(random.nextInt(players.size()));
    }

    private Player selectWeightedSkater(List<Player> players) {
        List<Player> eligiblePlayers = players.stream()
                .filter(player -> player.getPosition() != PlayerPosition.GOALIE)
                .filter(player -> player.getScoringWeight() != null)
                .filter(player -> player.getScoringWeight() > 0)
                .toList();

        if (eligiblePlayers.isEmpty()) {
            throw new IllegalStateException("No eligible skaters found for event generation");
        }

        int totalWeight = eligiblePlayers.stream()
                .mapToInt(Player::getScoringWeight)
                .sum();

        int randomValue = random.nextInt(totalWeight);

        int currentWeight = 0;

        for (Player player : eligiblePlayers) {
            currentWeight += player.getScoringWeight();

            if (randomValue < currentWeight) {
                return player;
            }
        }

        return eligiblePlayers.get(eligiblePlayers.size() - 1);
    }

}