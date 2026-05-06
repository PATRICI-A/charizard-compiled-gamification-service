package com.charizad.compiled.gamification_service.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class EarnedBadge {

    private final String badgeId;
    private final String badgeName;
    private final LocalDateTime earnedAt;
    private final int xpAwarded;
}
