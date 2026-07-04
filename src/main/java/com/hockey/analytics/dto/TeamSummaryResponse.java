package com.hockey.analytics.dto;

import java.util.UUID;

public record TeamSummaryResponse(
        UUID id,
        String name
) {
}
