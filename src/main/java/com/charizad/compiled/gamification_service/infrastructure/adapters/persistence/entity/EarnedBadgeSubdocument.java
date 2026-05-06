package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EarnedBadgeSubdocument {
    private String badgeId;
    private String badgeName;
    private LocalDateTime earnedAt;
    private int xpAwarded;
}
