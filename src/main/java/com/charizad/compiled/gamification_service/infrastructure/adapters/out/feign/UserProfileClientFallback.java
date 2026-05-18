package com.charizad.compiled.gamification_service.infrastructure.adapters.out.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Fallback del cliente OpenFeign para el servicio de perfiles.
 * Se activa cuando el servicio no está disponible o la URL no está configurada.
 * Devuelve {@link Optional#empty()} para que el caller use el userId como fallback.
 */
@Slf4j
@Component
public class UserProfileClientFallback implements UserProfileClient {

    @Override
    public Optional<String> getDisplayName(String userId) {
        log.debug("[ProfileClient] Fallback triggered for userId={} — profile service unavailable", userId);
        return Optional.empty();
    }
}
