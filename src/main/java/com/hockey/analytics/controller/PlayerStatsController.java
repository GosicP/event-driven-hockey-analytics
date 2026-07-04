package com.hockey.analytics.controller;

import com.hockey.analytics.dto.TopScorerResponse;
import com.hockey.analytics.service.LeagueStatsQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerStatsController {

    private final LeagueStatsQueryService leagueStatsQueryService;

    public PlayerStatsController(LeagueStatsQueryService leagueStatsQueryService) {
        this.leagueStatsQueryService = leagueStatsQueryService;
    }

    @GetMapping("/top-scorers")
    public List<TopScorerResponse> getTopScorers() {
        return leagueStatsQueryService.getTopScorers();
    }
}
