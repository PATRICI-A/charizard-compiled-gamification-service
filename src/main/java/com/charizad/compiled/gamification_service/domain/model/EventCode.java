package com.charizad.compiled.gamification_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class EventCode {

    private final UUID id;
    private final String code;
    private final LocalDateTime validFrom;
    private final LocalDateTime validUntil;
    private final List<String> usedByUserIds;

    public boolean isValid(LocalDateTime now) {
        return !now.isBefore(validFrom) && !now.isAfter(validUntil);
    }

    public boolean isUsedBy(String userId) {
        return usedByUserIds.contains(userId);
    }

    public void markUsedBy(String userId) {
        usedByUserIds.add(userId);
    }

    public List<String> getUsedByUserIds() {
        return Collections.unmodifiableList(usedByUserIds);
    }

    public static EventCode newCode(String code, LocalDateTime validFrom, LocalDateTime validUntil) {
        return EventCode.builder()
                .code(code)
                .validFrom(validFrom)
                .validUntil(validUntil)
                .usedByUserIds(new ArrayList<>())
                .build();
    }
}
