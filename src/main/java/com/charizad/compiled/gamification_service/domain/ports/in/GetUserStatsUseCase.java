package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;

public interface GetUserStatsUseCase {
    UserStatsResponse execute(String userId);
}
