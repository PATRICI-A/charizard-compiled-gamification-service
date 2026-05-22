package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BadgeDocumentMapper {

    public Badge toDomain(BadgeDocument doc) {
        return Badge.builder()
                .id(doc.getId())
                .name(doc.getName())
                .description(doc.getDescription())
                .category(doc.getCategory())
                .xpReward(doc.getXpReward())
                .iconUrl(doc.getIconUrl())
                .createdAt(doc.getCreatedAt())
                .active(doc.isActive())
                .build();
    }

    public BadgeDocument toDocument(Badge badge) {
        return BadgeDocument.builder()
                .id(badge.getId() != null ? badge.getId() : UUID.randomUUID())
                .name(badge.getName())
                .description(badge.getDescription())
                .category(badge.getCategory())
                .xpReward(badge.getXpReward())
                .iconUrl(badge.getIconUrl())
                .createdAt(badge.getCreatedAt())
                .active(badge.isActive())
                .build();
    }
}
