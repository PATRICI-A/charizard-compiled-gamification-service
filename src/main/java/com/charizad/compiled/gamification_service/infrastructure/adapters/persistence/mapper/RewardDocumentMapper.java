package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.Reward;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.RewardDocument;
import org.springframework.stereotype.Component;

@Component
public class RewardDocumentMapper {

    public Reward toDomain(RewardDocument doc) {
        return Reward.builder()
                .id(doc.getId())
                .name(doc.getName())
                .description(doc.getDescription())
                .type(doc.getType())
                .xpThreshold(doc.getXpThreshold())
                .iconUrl(doc.getIconUrl())
                .createdAt(doc.getCreatedAt())
                .active(doc.isActive())
                .build();
    }

    public RewardDocument toDocument(Reward reward) {
        return RewardDocument.builder()
                .id(reward.getId())
                .name(reward.getName())
                .description(reward.getDescription())
                .type(reward.getType())
                .xpThreshold(reward.getXpThreshold())
                .iconUrl(reward.getIconUrl())
                .createdAt(reward.getCreatedAt())
                .active(reward.isActive())
                .build();
    }
}
