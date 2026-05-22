package com.charizad.compiled.gamification_service.domain.exceptions;

public class MonaNotFoundException extends RuntimeException {

    public MonaNotFoundException(String MonaId) {
        super("Insignia no encontrada con id: " + MonaId);
    }
}
