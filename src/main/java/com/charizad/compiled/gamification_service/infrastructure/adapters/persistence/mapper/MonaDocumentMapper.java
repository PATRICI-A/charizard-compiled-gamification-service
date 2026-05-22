package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.MonaDocument;
import org.springframework.stereotype.Component;

@Component
public class MonaDocumentMapper {

    public Mona toDomain(MonaDocument doc) {
        return Mona.builder()
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

    public MonaDocument toDocument(Mona Mona) {
        return MonaDocument.builder()
                .id(Mona.getId())
                .name(Mona.getName())
                .description(Mona.getDescription())
                .category(Mona.getCategory())
                .xpReward(Mona.getXpReward())
                .iconUrl(Mona.getIconUrl())
                .createdAt(Mona.getCreatedAt())
                .active(Mona.isActive())
                .build();
    }
}
