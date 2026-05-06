package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;

import java.util.List;

public interface GetUserBadgesUseCase {
    List<EarnedBadgeResponse> execute(String userId);
}
