package com.charizad.compiled.gamification_service.domain.exceptions;

public class BadgeAlreadyEarnedException extends RuntimeException {

    public BadgeAlreadyEarnedException(String userId, String badgeId) {
        super("El usuario " + userId + " ya posee la insignia: " + badgeId);
    }
}
