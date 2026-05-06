package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.BadgeProgressResponse;

import java.util.List;

public interface GetUserProgressUseCase {
    List<BadgeProgressResponse> execute(String userId);
}
