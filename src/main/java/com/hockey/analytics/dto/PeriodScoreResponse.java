package com.hockey.analytics.dto;

public record PeriodScoreResponse(
        Integer period,
        Integer homeScore,
        Integer awayScore
) {
}
