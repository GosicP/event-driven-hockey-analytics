package com.hockey.analytics.controller;

import com.hockey.analytics.dto.GameDetailsResponse;
import com.hockey.analytics.dto.GameEventResponse;
import com.hockey.analytics.dto.GameSummaryResponse;
import com.hockey.analytics.service.GameQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameQueryController {

    private final GameQueryService gameQueryService;

    public GameQueryController(GameQueryService gameQueryService) {
        this.gameQueryService = gameQueryService;
    }

    @GetMapping
    public List<GameSummaryResponse> listGames() {
        return gameQueryService.listGames();
    }

    @GetMapping("/{gameId}")
    public GameDetailsResponse getGameDetails(@PathVariable UUID gameId) {
        return gameQueryService.getGameDetails(gameId);
    }

    @GetMapping("/{gameId}/events")
    public List<GameEventResponse> getEvents(
            @PathVariable UUID gameId,
            @RequestParam(required = false) Integer afterSequenceNumber
    ) {
        return gameQueryService.getEvents(gameId, afterSequenceNumber);
    }
}
