package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity;

import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
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
public class EarnedRewardSubdocument {

    private UUID rewardId;
    private String rewardName;
    private RewardType rewardType;
    private LocalDateTime unlockedAt;
    private int xpAtUnlock;
}
