package com.charizad.compiled.gamification_service.application.dto.response;

import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonaResponse {
    private UUID monaId;
    private String name;
    private String description;
    private BadgeCategory rarity;
    private boolean unlocked;
    private LocalDate earnedAt;
    private int currentCount;
    private int targetCount;
    private float progressPercentage;
}
