package com.charizad.compiled.gamification_service.application.dto.request;

import com.charizad.compiled.gamification_service.domain.model.BadgeUnlockEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Evento de acción del usuario enviado por otros microservicios para
 * disparar la evaluación de desbloqueo de monas (RF13.1.1).
 *
 * Cada servicio que detecta una acción relevante llama a
 * POST /api/v1/gamificacion/events/{eventType} pasando este DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeUnlockEventRequest {

    @NotBlank(message = "userId es obligatorio")
    private String userId;

    @NotNull(message = "eventType es obligatorio")
    private BadgeUnlockEventType eventType;

    /** Conteo acumulado de conexiones activas del usuario (para monas #1, #2, #3, #12). */
    private Integer totalActiveConnections;

    /** Fecha de registro del usuario (para Meteoro Social #12). */
    private LocalDateTime userRegisteredAt;

    /**
     * true si el usuario es el capitán (creador) del parche.
     * false si se unió como miembro.
     * Relevante para monas #4 (Primer Parche), #5 (Anfitrión), #6 (Planificador), #11 (Imán Social).
     */
    private Boolean isCreator;

    /**
     * Número total de parches creados por el usuario como capitán.
     * Relevante para mona #5 (Anfitrión).
     */
    private Integer totalParchesCreated;

    /**
     * Fecha programada del parche (para Planificador #6 — >3 días de anticipación).
     */
    private LocalDateTime parcheScheduledAt;

    /**
     * Zona del campus visitada (ej: "Bloque de Ingeniería").
     * Recibida del geo service vía geo.exchange / geo.location.updated.
     * Gamification trackea zonas distintas internamente para Explorador I (#7) y II (#8).
     */
    private String campusZone;

    /**
     * true si el usuario tiene la geolocalización habilitada (RN-13.1.5).
     * Siempre true cuando el evento proviene del geo service (el evento solo se publica
     * si el usuario compartió ubicación exitosamente).
     */
    private Boolean geoLocationEnabled;

    /**
     * ID del capitán del parche al que se unió el usuario.
     * Usado para otorgar Imán Social (#11) al capitán cuando alguien se une a su parche.
     */
    private String captainUserId;
}
