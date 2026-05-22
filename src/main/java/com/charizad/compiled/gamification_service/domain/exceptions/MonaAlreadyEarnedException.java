package com.charizad.compiled.gamification_service.domain.exceptions;

public class MonaAlreadyEarnedException extends RuntimeException {

    public MonaAlreadyEarnedException(String userId, String MonaId) {
        super("El usuario " + userId + " ya posee la insignia: " + MonaId);
    }
}
