package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.request.CreateBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.BadgeResponse;

public interface CreateBadgeUseCase {
    BadgeResponse execute(CreateBadgeRequest request);
}
