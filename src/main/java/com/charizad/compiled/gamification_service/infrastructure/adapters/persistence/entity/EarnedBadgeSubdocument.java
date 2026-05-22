package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EarnedBadgeSubdocument {
    private UUID badgeId;
    private String badgeName;
    private LocalDateTime earnedAt;
    private int xpAwarded;
}
