package com.charizad.compiled.gamification_service.domain.model;

import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Badge {

    private final UUID id;
    private final String name;
    private final String description;
    private final BadgeCategory category;
    private final int xpReward;
    private final String iconUrl;
    private final LocalDateTime createdAt;
    private final boolean active;
}
