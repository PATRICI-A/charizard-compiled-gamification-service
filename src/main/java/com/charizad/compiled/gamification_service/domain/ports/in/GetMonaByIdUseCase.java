package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.MonaDetailResponse;

public interface GetMonaByIdUseCase {
    MonaDetailResponse execute(String userId, String monaId);
}
