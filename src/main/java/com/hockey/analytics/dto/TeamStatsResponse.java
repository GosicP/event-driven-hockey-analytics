package com.hockey.analytics.dto;

import java.util.UUID;

public record TeamStatsResponse(
        UUID teamId,
        String teamName,
        Integer shots,
        Integer goals,
        Integer penalties
) {
}