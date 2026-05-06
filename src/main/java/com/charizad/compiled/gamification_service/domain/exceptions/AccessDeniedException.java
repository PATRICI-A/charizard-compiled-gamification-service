package com.charizad.compiled.gamification_service.domain.exceptions;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public static AccessDeniedException adminOnly() {
        return new AccessDeniedException("Esta operación requiere rol ADMIN.");
    }
}
