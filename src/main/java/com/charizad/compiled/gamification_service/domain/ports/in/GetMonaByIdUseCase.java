package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;

public interface GetMonaByIdUseCase {
    MonaResponse execute(String userId, String monaId);
}
