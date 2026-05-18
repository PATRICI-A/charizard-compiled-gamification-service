package com.charizad.compiled.gamification_service.infrastructure.adapters.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

/**
 * Cliente OpenFeign para obtener datos del servicio de perfiles (M03).
 *
 * La URL se configura via la propiedad {@code services.profile.url}.
 * Si la URL no está configurada o el servicio no responde, los métodos
 * devuelven {@link Optional#empty()} gracias al fallback configurado en
 * {@link UserProfileClientFallback}.
 *
 * <p>Estado: boceto — integración pendiente hasta que M03 esté disponible.</p>
 */
@FeignClient(
        name = "profile-service",
        url = "${services.profile.url:}",
        fallback = UserProfileClientFallback.class
)
public interface UserProfileClient {

    /**
     * Obtiene el nombre visible (display name) de un usuario.
     *
     * @param userId ID del usuario
     * @return Optional con el displayName, vacío si el servicio no está disponible
     */
    @GetMapping("/api/v1/profiles/{userId}/display-name")
    Optional<String> getDisplayName(@PathVariable("userId") String userId);
}
