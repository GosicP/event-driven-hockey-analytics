package com.hockey.analytics.dto;

import java.util.UUID;

public record PlayerStatsResponse(
        UUID playerId,
        String playerName,
        String teamName,
        String position,
        Integer shots,
        Integer goals,
        Integer penalties
) {
}