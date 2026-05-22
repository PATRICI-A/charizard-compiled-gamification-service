package com.charizad.compiled.gamification_service.application.dto.response;

import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonaDetailResponse {
    private String monaId;
    private String name;
    private String description;
    private MonaCategory rarity;
    private boolean unlocked;
    private LocalDate earnedAt;
    private int currentCount;
    private int targetCount;
    private float progressPercentage;
}
