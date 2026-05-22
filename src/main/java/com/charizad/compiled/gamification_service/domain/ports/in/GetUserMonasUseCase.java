package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;

import java.util.List;

public interface GetUserMonasUseCase {
    List<EarnedMonaResponse> execute(String userId);
}
