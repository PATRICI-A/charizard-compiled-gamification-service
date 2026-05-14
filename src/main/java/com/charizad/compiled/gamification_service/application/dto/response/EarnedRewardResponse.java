package com.charizad.compiled.gamification_service.application.dto.response;

import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EarnedRewardResponse {

    private String rewardId;
    private String rewardName;
    private RewardType rewardType;
    private LocalDateTime unlockedAt;
    private int xpAtUnlock;
}
