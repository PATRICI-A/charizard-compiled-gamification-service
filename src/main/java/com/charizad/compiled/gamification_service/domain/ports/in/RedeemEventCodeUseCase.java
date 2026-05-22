package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;

public interface RedeemEventCodeUseCase {
    EarnedMonaResponse execute(String userId, String eventCode);
}
