package com.charizad.compiled.gamification_service.domain.valueobjects;

import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Immutable record of a reward that a user has already unlocked.
 * Stored as an embedded list inside {@code UserGamification}.
 */
@Getter
@Builder
@AllArgsConstructor
public class EarnedReward {

    private final String rewardId;
    private final String rewardName;
    private final RewardType rewardType;
    private final LocalDateTime unlockedAt;

    /** The user's total XP at the moment the reward was unlocked. */
    private final int xpAtUnlock;
}
