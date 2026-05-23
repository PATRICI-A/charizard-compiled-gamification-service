package com.charizad.compiled.gamification_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLevelResponse {
    private String userId;
    private int currentLevel;
    private String levelName;
    private int totalMonasEarned;
    private int totalXP;
    private Integer xpForNextLevel;
    private Integer xpRemaining;
    private float progressPercentage;
    private boolean isMaxLevel;
    private String currentReward;
}
