package com.hockey.analytics.dto;

import java.util.List;
import java.util.UUID;

public record GameStatsResponse(
        UUID gameId,
        String homeTeam,
        String awayTeam,
        List<TeamStatsResponse> teamStats,
        List<PlayerStatsResponse> playerStats
) {
}