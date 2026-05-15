package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
import org.springframework.stereotype.Component;

@Component
public class BadgeDocumentMapper {

    public BadgeDocument toDocument(Badge domain) {
        if (domain == null) return null;
        return BadgeDocument.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .xpReward(domain.getXpReward())
                .iconUrl(domain.getIconUrl())
                .createdAt(domain.getCreatedAt())
                .active(domain.isActive())
                .build();
    }

    public Badge toDomain(BadgeDocument entity) {
        if (entity == null) return null;
        return Badge.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .xpReward(entity.getXpReward())
                .iconUrl(entity.getIconUrl())
                .createdAt(entity.getCreatedAt())
                .active(entity.isActive())
                .build();
    }
}
