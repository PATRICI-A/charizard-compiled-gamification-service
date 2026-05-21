package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;

public interface GetRankingPositionUseCase {
    RankingPositionResponse execute(String userId, RankingType type);
}
