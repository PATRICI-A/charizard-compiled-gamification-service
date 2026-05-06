package com.charizad.compiled.gamification_service.domain.exceptions;

public class UserGamificationNotFoundException extends RuntimeException {

    public UserGamificationNotFoundException(String userId) {
        super("Perfil de gamificación no encontrado para el usuario: " + userId);
    }
}
