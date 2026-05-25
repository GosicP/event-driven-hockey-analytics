package com.hockey.analytics.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameAlreadySimulatedException.class)
    public ResponseEntity<ApiErrorResponse> handleGameAlreadySimulated(GameAlreadySimulatedException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("GAME_ALREADY_SIMULATED", ex.getMessage()));
    }
}