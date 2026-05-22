package com.charizad.compiled.gamification_service.entrypoints.rest.mapper;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.request.CreateBadgeRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper para transformar parámetros del controller a DTOs de request.
 * Los DTOs de response se usan directamente desde la capa application.
 */
@Component
public class BadgeRestMapper {

    public CreateBadgeRequest toCreateRequest(CreateBadgeRequest body) {
        return body;
    }

    public AwardBadgeRequest toAwardRequest(String userId, String badgeId) {
        return AwardBadgeRequest.builder()
                .userId(userId)
                .badgeId(UUID.fromString(badgeId))
                .build();
    }
}
