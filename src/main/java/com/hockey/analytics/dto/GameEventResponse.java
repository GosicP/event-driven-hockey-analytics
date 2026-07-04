package com.hockey.analytics.dto;

import java.util.UUID;

public record GameEventResponse(
        UUID id,
        Integer sequenceNumber,
        Integer period,
        String type,
        UUID teamId,
        String teamName,
        UUID playerId,
        String playerName,
        Integer homeScoreAfterEvent,
        Integer awayScoreAfterEvent
) {
}
