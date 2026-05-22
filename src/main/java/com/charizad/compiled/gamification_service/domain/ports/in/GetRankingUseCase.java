package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;

import java.util.List;

public interface GetRankingUseCase {
    List<RankingEntryResponse> execute(RankingType type, int limit);
}
