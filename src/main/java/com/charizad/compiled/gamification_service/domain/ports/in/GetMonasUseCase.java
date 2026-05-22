package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.MonaDetailResponse;

import java.util.List;

public interface GetMonasUseCase {
    List<MonaDetailResponse> execute(String userId);
}
