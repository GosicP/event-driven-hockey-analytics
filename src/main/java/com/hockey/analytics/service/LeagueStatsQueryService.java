package com.hockey.analytics.service;

import com.hockey.analytics.dto.TopScorerResponse;
import com.hockey.analytics.model.Player;
import com.hockey.analytics.model.PlayerGameStats;
import com.hockey.analytics.repository.PlayerGameStatsRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LeagueStatsQueryService {

    private final PlayerGameStatsRepository playerGameStatsRepository;

    public LeagueStatsQueryService(PlayerGameStatsRepository playerGameStatsRepository) {
        this.playerGameStatsRepository = playerGameStatsRepository;
    }

    // sabira player_game_stats redove po igracu, preko SVIH utakmica u ligi
    public List<TopScorerResponse> getTopScorers() {
        Map<UUID, Totals> totalsByPlayer = new LinkedHashMap<>();

        for (PlayerGameStats stats : playerGameStatsRepository.findAll()) {
            Player player = stats.getPlayer();
            Totals totals = totalsByPlayer.computeIfAbsent(
                    player.getPlayerId(),
                    id -> new Totals(player.getName(), player.getTeam().getName(), player.getPosition().name())
            );
            totals.shots += stats.getShots();
            totals.goals += stats.getGoals();
            totals.penalties += stats.getPenalties();
        }

        return totalsByPlayer.entrySet().stream()
                .map(entry -> new TopScorerResponse(
                        entry.getKey(),
                        entry.getValue().playerName,
                        entry.getValue().teamName,
                        entry.getValue().position,
                        entry.getValue().shots,
                        entry.getValue().goals,
                        entry.getValue().penalties
                ))
                .sorted(Comparator
                        .comparing(TopScorerResponse::goals).reversed()
                        .thenComparing(TopScorerResponse::shots, Comparator.reverseOrder()))
                .toList();
    }

    private static class Totals {
        final String playerName;
        final String teamName;
        final String position;
        int shots;
        int goals;
        int penalties;

        Totals(String playerName, String teamName, String position) {
            this.playerName = playerName;
            this.teamName = teamName;
            this.position = position;
        }
    }
}
