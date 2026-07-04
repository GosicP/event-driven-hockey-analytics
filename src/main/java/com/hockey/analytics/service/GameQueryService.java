package com.hockey.analytics.service;

import com.hockey.analytics.dto.GameDetailsResponse;
import com.hockey.analytics.dto.GameEventResponse;
import com.hockey.analytics.dto.GameSummaryResponse;
import com.hockey.analytics.dto.PeriodScoreResponse;
import com.hockey.analytics.dto.TeamGameScoreResponse;
import com.hockey.analytics.dto.TeamSummaryResponse;
import com.hockey.analytics.exception.GameNotFoundException;
import com.hockey.analytics.model.EventType;
import com.hockey.analytics.model.Game;
import com.hockey.analytics.model.GameEvent;
import com.hockey.analytics.model.Player;
import com.hockey.analytics.repository.GameEventRepository;
import com.hockey.analytics.repository.GameRepository;
import com.hockey.analytics.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameQueryService {

    private final GameRepository gameRepository;
    private final GameEventRepository gameEventRepository;
    private final PlayerRepository playerRepository;

    public GameQueryService(GameRepository gameRepository,
                            GameEventRepository gameEventRepository,
                            PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.gameEventRepository = gameEventRepository;
        this.playerRepository = playerRepository;
    }

    public List<GameSummaryResponse> listGames() {
        // učitaj sve utakmice iz baze (za početnu listu)
        return gameRepository.findAll().stream()
                .map(game -> {
                    // učitaj evente ove utakmice, sortirane po sequenceNumber
                    List<GameEvent> events = gameEventRepository.findByGameIdOrderBySequenceNumber(game.getGameId());
                    // iz eventova izračunaj status, trenutnu trećinu i rezultat (Game entitet ih nema)
                    GameProgress progress = GameProgress.compute(game, events);

                    return new GameSummaryResponse(
                            game.getGameId(),
                            new TeamSummaryResponse(game.getHomeTeam().getTeamId(), game.getHomeTeam().getName()),
                            new TeamSummaryResponse(game.getAwayTeam().getTeamId(), game.getAwayTeam().getName()),
                            progress.homeScore(),
                            progress.awayScore(),
                            progress.status(),
                            progress.currentPeriod(),
                            game.getGameDate()
                    );
                })
                .toList();
    }

    public GameDetailsResponse getGameDetails(UUID gameId) {
        // nađi utakmicu ili baci 404 ako ne postoji
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        List<GameEvent> events = gameEventRepository.findByGameIdOrderBySequenceNumber(gameId);
        // ista logika izračunavanja kao u listGames(), samo za jednu utakmicu
        GameProgress progress = GameProgress.compute(game, events);

        return new GameDetailsResponse(
                game.getGameId(),
                progress.status(),
                progress.currentPeriod(),
                new TeamGameScoreResponse(game.getHomeTeam().getTeamId(), game.getHomeTeam().getName(), progress.homeScore()),
                new TeamGameScoreResponse(game.getAwayTeam().getTeamId(), game.getAwayTeam().getName(), progress.awayScore()),
                progress.periodScores(),
                game.getGameDate()
        );
    }

    public List<GameEventResponse> getEvents(UUID gameId, Integer afterSequenceNumber) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        // svi eventi od početka utakmice - moraju svi da se pročitaju da bi rezultat bio tačan i za novije evente
        List<GameEvent> events = gameEventRepository.findByGameIdOrderBySequenceNumber(gameId);

        UUID homeTeamId = game.getHomeTeam().getTeamId();
        UUID awayTeamId = game.getAwayTeam().getTeamId();

        // ime tima po id-u, bez odlaska u bazu po eventu
        Map<UUID, String> teamNames = new HashMap<>();
        teamNames.put(homeTeamId, game.getHomeTeam().getName());
        teamNames.put(awayTeamId, game.getAwayTeam().getName());

        // skupi sve id-jeve igrača koji se pominju u eventima, bez duplikata
        Set<UUID> playerIds = events.stream()
                .map(GameEvent::getPlayerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // učitaj sva imena igrača JEDNIM upitom (izbegava upit u bazu za svaki event posebno)
        Map<UUID, String> playerNames = playerRepository.findAllById(playerIds).stream()
                .collect(Collectors.toMap(Player::getPlayerId, Player::getName));

        List<GameEventResponse> responses = new ArrayList<>();
        // tekući rezultat dok "prolazimo kroz vreme" event po event
        int homeScore = 0;
        int awayScore = 0;

        for (GameEvent event : events) {
            // ako je gol, uvećaj rezultat ODGOVARAJUĆEG tima PRE filtriranja ispod -
            // mora ovako jer i eventi POSLE granice moraju da znaju tačan rezultat do tog trenutka
            if (event.getEventType() == EventType.GOAL) {
                if (homeTeamId.equals(event.getTeamId())) {
                    homeScore++;
                } else if (awayTeamId.equals(event.getTeamId())) {
                    awayScore++;
                }
            }

            // preskoči evente koje klijent već ima (sve do afterSequenceNumber uključujući njega)
            if (afterSequenceNumber != null && event.getSequenceNumber() <= afterSequenceNumber) {
                continue;
            }

            // dodaj event u odgovor sa rezultatom kakav je bio TAČNO u tom trenutku
            responses.add(new GameEventResponse(
                    event.getEventId(),
                    event.getSequenceNumber(),
                    event.getPeriodNumber(),
                    event.getEventType().name(),
                    event.getTeamId(),
                    event.getTeamId() != null ? teamNames.get(event.getTeamId()) : null,
                    event.getPlayerId(),
                    event.getPlayerId() != null ? playerNames.get(event.getPlayerId()) : null,
                    homeScore,
                    awayScore
            ));
        }

        return responses;
    }

    // pomoćna klasa - izračunava sve što Game entitet nema sačuvano (status, trećina, rezultat)
    private record GameProgress(
            String status,
            Integer currentPeriod,
            Integer homeScore,
            Integer awayScore,
            List<PeriodScoreResponse> periodScores
    ) {
        static GameProgress compute(Game game, List<GameEvent> events) {
            // da li postoji GAME_STARTED event (utakmica je bar počela)
            boolean started = events.stream().anyMatch(e -> e.getEventType() == EventType.GAME_STARTED);
            // da li postoji GAME_ENDED event (utakmica je gotova)
            boolean ended = events.stream().anyMatch(e -> e.getEventType() == EventType.GAME_ENDED);

            // nema početka -> zakazana; ima početak bez kraja -> u toku; ima i kraj -> završena
            String status = !started ? "SCHEDULED" : (ended ? "FINISHED" : "IN_PROGRESS");

            // trenutna trećina = period POSLEDNJEG eventa koji uopšte ima period
            // (GAME_STARTED/GAME_ENDED nemaju period, zato se filtriraju)
            Integer currentPeriod = events.stream()
                    .filter(e -> e.getPeriodNumber() != null)
                    .max(Comparator.comparing(GameEvent::getSequenceNumber))
                    .map(GameEvent::getPeriodNumber)
                    .orElse(null);

            UUID homeTeamId = game.getHomeTeam().getTeamId();
            UUID awayTeamId = game.getAwayTeam().getTeamId();

            // prvi prolaz: zapamti KOJE trećine su uopšte počele (da bi odigrana trećina bez golova
            // ostala u tabeli kao 0-0, umesto da bude izostavljena)
            Map<Integer, int[]> periodGoals = new TreeMap<>();
            for (GameEvent event : events) {
                if (event.getEventType() == EventType.PERIOD_STARTED) {
                    periodGoals.putIfAbsent(event.getPeriodNumber(), new int[2]);
                }
            }

            int homeScore = 0;
            int awayScore = 0;

            // drugi prolaz: prebroj golove, ukupno i po trećini u kojoj su pali
            for (GameEvent event : events) {
                if (event.getEventType() != EventType.GOAL) {
                    continue;
                }

                // da li je gol dao domaći ili gostujući tim
                boolean isHome = homeTeamId.equals(event.getTeamId());
                boolean isAway = awayTeamId.equals(event.getTeamId());

                // ukupan rezultat utakmice
                if (isHome) {
                    homeScore++;
                } else if (isAway) {
                    awayScore++;
                }

                // rezultat SAMO za tu trećinu (za P1/P2/P3 tabelu)
                int[] counts = periodGoals.computeIfAbsent(event.getPeriodNumber(), p -> new int[2]);
                if (isHome) {
                    counts[0]++;
                } else if (isAway) {
                    counts[1]++;
                }
            }

            // pretvori mapu trećina u sortiranu listu (TreeMap već drži periode po redu: 1, 2, 3)
            List<PeriodScoreResponse> periodScores = periodGoals.entrySet().stream()
                    .map(e -> new PeriodScoreResponse(e.getKey(), e.getValue()[0], e.getValue()[1]))
                    .toList();

            return new GameProgress(status, currentPeriod, homeScore, awayScore, periodScores);
        }
    }
}
