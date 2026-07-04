package com.hockey.analytics;

import com.hockey.analytics.service.GameSimulationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SimulationRunner implements CommandLineRunner {

    private final GameSimulationService gameSimulationService;

    public SimulationRunner(GameSimulationService gameSimulationService) {
        this.gameSimulationService = gameSimulationService;
    }

    @Override
    public void run(String... args) {
//        gameSimulationService.simulateGame(
//                UUID.fromString("90000000-0000-0000-0000-000000000001")
//        );
    }
}