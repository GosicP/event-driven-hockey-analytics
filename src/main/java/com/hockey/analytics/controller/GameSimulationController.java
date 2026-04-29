package com.hockey.analytics.controller;

import com.hockey.analytics.service.GameSimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameSimulationController {
    private final GameSimulationService gameSimulationService;

    public GameSimulationController(GameSimulationService gameSimulationService) {
        this.gameSimulationService = gameSimulationService;
    }

    @PostMapping("/{gameId}/simulate")
    public ResponseEntity<Void> simulateGame(@PathVariable UUID gameId) {
        gameSimulationService.simulateGame(gameId);
        return ResponseEntity.noContent().build();
    }
}
