package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.request.CreateRewardRequest;
import com.charizad.compiled.gamification_service.application.dto.response.RewardResponse;

public interface CreateRewardUseCase {

    RewardResponse execute(CreateRewardRequest request);
}
