package com.charizad.compiled.gamification_service.domain.exceptions;

public class BadgeNotFoundException extends RuntimeException {

    public BadgeNotFoundException(String badgeId) {
        super("Insignia no encontrada con id: " + badgeId);
    }
}
