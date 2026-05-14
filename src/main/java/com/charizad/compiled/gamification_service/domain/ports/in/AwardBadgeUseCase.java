package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;

/**
 * Puerto de entrada para otorgar una insignia a un usuario.
 * Pendiente de integración con eventos del M02 (Gestión de Parches).
 * Por ahora puede ser invocado manualmente por un administrador.
 */
public interface AwardBadgeUseCase {
    EarnedBadgeResponse execute(AwardBadgeRequest request);
}
