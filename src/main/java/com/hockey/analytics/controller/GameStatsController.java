package com.hockey.analytics.controller;

import com.hockey.analytics.dto.GameStatsResponse;
import com.hockey.analytics.service.GameStatsQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameStatsController {

    private final GameStatsQueryService gameStatsQueryService;

    public GameStatsController(GameStatsQueryService gameStatsQueryService) {
        this.gameStatsQueryService = gameStatsQueryService;
    }

    @GetMapping("/{gameId}/stats")
    public GameStatsResponse getGameStats(@PathVariable UUID gameId) {
        return gameStatsQueryService.getGameStats(gameId);
    }
}