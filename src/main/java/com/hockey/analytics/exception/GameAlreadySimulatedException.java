package com.hockey.analytics.exception;

import java.util.UUID;

public class GameAlreadySimulatedException extends RuntimeException {

    public GameAlreadySimulatedException(UUID gameId) {
        super("Game already simulated: " + gameId);
    }
}