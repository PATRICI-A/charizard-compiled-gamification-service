package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;

public interface GetRankingPositionUseCase {
    RankingPositionResponse execute(String userId);
}
