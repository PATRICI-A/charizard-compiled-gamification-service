package com.charizad.compiled.gamification_service.entrypoints.rest.mapper;

import com.charizad.compiled.gamification_service.application.dto.request.ToggleRankingOptInRequest;
import org.springframework.stereotype.Component;

@Component
public class UserGamificationRestMapper {

    public ToggleRankingOptInRequest toToggleRequest(String userId) {
        return ToggleRankingOptInRequest.builder()
                .userId(userId)
                .build();
    }
}
