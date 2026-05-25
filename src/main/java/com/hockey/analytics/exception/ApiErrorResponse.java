package com.hockey.analytics.exception;

public record ApiErrorResponse(
        String code,
        String message
) {
}