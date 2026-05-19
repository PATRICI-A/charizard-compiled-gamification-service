package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.EventCode;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EventCodeDocument;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class EventCodeDocumentMapper {

    public EventCode toDomain(EventCodeDocument doc) {
        return EventCode.builder()
                .id(doc.getId())
                .code(doc.getCode())
                .validFrom(doc.getValidFrom())
                .validUntil(doc.getValidUntil())
                .usedByUserIds(new ArrayList<>(doc.getUsedByUserIds() != null ? doc.getUsedByUserIds() : new ArrayList<>()))
                .build();
    }

    public EventCodeDocument toDocument(EventCode eventCode) {
        return EventCodeDocument.builder()
                .id(eventCode.getId())
                .code(eventCode.getCode())
                .validFrom(eventCode.getValidFrom())
                .validUntil(eventCode.getValidUntil())
                .usedByUserIds(new ArrayList<>(eventCode.getUsedByUserIds()))
                .build();
    }
}
