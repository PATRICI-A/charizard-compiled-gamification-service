package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.request.AwardMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;

/**
 * Puerto de entrada para otorgar una insignia a un usuario.
 * Pendiente de integración con eventos del M02 (Gestión de Parches).
 * Por ahora puede ser invocado manualmente por un administrador.
 */
public interface AwardMonaUseCase {
    EarnedMonaResponse execute(AwardMonaRequest request);
}
