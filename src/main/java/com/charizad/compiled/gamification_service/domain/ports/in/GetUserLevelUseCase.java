package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.UserLevelResponse;

public interface GetUserLevelUseCase {
    UserLevelResponse execute(String userId);
}
