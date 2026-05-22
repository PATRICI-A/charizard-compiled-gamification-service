package com.charizad.compiled.gamification_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private String userId;
    private int totalXp;
    private int weeklyXp;
    private int weeklyMonas;
    private int totalMonas;
    private int nivel;
    private String levelName;
    private boolean rankingOptIn;
    private int totalMonasEarned;
    private int totalRewardsUnlocked;
}
