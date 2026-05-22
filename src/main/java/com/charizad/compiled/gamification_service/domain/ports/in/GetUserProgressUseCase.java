package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.MonaProgressResponse;

import java.util.List;

public interface GetUserProgressUseCase {
    List<MonaProgressResponse> execute(String userId);
}
