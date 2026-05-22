package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeProgressSubdocument {
    private UUID badgeId;
    private int currentValue;
    private int requiredValue;
    private boolean completed;
}
