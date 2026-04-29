package com.hockey.analytics.controller;

import com.hockey.analytics.service.GameReplayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameReplayController {
    private final GameReplayService gameReplayService;

    public GameReplayController(GameReplayService gameReplayService) {
        this.gameReplayService = gameReplayService;
    }

    @PostMapping("/{gameId}/replay")
    public ResponseEntity<Void> replayGame(@PathVariable UUID gameId) {
        gameReplayService.replayGame(gameId);
        return ResponseEntity.noContent().build();
    }
}
