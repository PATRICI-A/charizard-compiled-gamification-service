package com.charizad.compiled.gamification_service.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Progreso de un usuario hacia el desbloqueo de una insignia específica.
 * La lógica de evaluación del criterio queda pendiente hasta integración con M02.
 */
@Getter
@Builder
@AllArgsConstructor
public class BadgeProgress {

    private final String badgeId;
    private final int currentValue;
    private final int requiredValue;
    private final boolean completed;
}
