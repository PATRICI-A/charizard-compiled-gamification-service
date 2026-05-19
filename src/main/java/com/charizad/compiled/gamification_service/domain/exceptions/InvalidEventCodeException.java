package com.charizad.compiled.gamification_service.domain.exceptions;

public class InvalidEventCodeException extends RuntimeException {
    public InvalidEventCodeException() {
        super("Código no válido o ya utilizado.");
    }
}
