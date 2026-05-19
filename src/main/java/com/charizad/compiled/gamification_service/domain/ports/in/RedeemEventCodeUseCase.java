package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;

public interface RedeemEventCodeUseCase {
    EarnedBadgeResponse execute(String userId, String eventCode);
}
