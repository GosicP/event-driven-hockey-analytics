package com.hockey.analytics;

import com.hockey.analytics.service.GameSimulationService;
import com.hockey.analytics.service.GameStatsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SimulationRunner implements CommandLineRunner {

    private final GameSimulationService gameSimulationService;
    private final GameStatsService gameStatsService;

    public SimulationRunner(GameSimulationService gameSimulationService, GameStatsService gameStatsService) {
        this.gameSimulationService = gameSimulationService;
        this.gameStatsService = gameStatsService;
    }

    @Override
    public void run(String... args) {
//        gameSimulationService.simulateGame(
//                UUID.fromString("90000000-0000-0000-0000-000000000001")
//        );
//        gameStatsService.calculateStats(UUID.fromString("90000000-0000-0000-0000-000000000001"));
    }
}