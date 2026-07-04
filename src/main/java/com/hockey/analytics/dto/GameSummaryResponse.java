package com.hockey.analytics.dto;

import java.time.Instant;
import java.util.UUID;

public record GameSummaryResponse(
        UUID id,
        TeamSummaryResponse homeTeam,
        TeamSummaryResponse awayTeam,
        Integer homeScore,
        Integer awayScore,
        String status,
        Integer currentPeriod,
        Instant scheduledAt
) {
}
