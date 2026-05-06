package com.charizad.compiled.gamification_service.application.mapper;

import com.charizad.compiled.gamification_service.application.dto.request.CreateBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.BadgeResponse;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BadgeMapper {

    public Badge toDomain(CreateBadgeRequest request) {
        return Badge.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .xpReward(request.getXpReward())
                .iconUrl(request.getIconUrl())
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    public BadgeResponse toResponse(Badge badge) {
        return BadgeResponse.builder()
                .id(badge.getId())
                .name(badge.getName())
                .description(badge.getDescription())
                .category(badge.getCategory())
                .xpReward(badge.getXpReward())
                .iconUrl(badge.getIconUrl())
                .createdAt(badge.getCreatedAt())
                .build();
    }
}
