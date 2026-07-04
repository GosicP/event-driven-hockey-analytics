package com.hockey.analytics.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GameDetailsResponse(
        UUID id,
        String status,
        Integer currentPeriod,
        TeamGameScoreResponse homeTeam,
        TeamGameScoreResponse awayTeam,
        List<PeriodScoreResponse> periodScores,
        Instant scheduledAt
) {
}
