package com.charizad.compiled.gamification_service.domain.model;

import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class Mona {

    private final String id;
    private final String name;
    private final String description;
    private final MonaCategory category;
    private final int xpReward;
    private final String iconUrl;
    private final LocalDateTime createdAt;
    private final boolean active;
}
