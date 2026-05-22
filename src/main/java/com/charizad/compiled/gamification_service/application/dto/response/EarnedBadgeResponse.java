package com.charizad.compiled.gamification_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EarnedBadgeResponse {
    private UUID badgeId;
    private String badgeName;
    private LocalDateTime earnedAt;
    private int xpAwarded;
}
