package com.charizad.compiled.gamification_service.domain.model;

import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A reward that is automatically unlocked when a user's total XP reaches
 * {@code xpThreshold}. No reward content exists yet; this model holds the
 * structure ready for future catalogue entries.
 */
@Getter
@Builder
@AllArgsConstructor
public class Reward {

    private final UUID id;
    private final String name;
    private final String description;
    private final RewardType type;

    /** Minimum accumulated XP required to unlock this reward. */
    private final int xpThreshold;

    private final String iconUrl;
    private final LocalDateTime createdAt;
    private final boolean active;
}
