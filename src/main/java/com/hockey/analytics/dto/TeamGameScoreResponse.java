package com.hockey.analytics.dto;

import java.util.UUID;

public record TeamGameScoreResponse(
        UUID id,
        String name,
        Integer score
) {
}
