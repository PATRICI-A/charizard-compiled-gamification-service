package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedRewardResponse;

import java.util.List;

public interface GetUserRewardsUseCase {

    List<EarnedRewardResponse> execute(String userId);
}
