package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;

import java.util.List;

public interface GetRankingUseCase {
    List<RankingEntryResponse> execute(int limit);
}
