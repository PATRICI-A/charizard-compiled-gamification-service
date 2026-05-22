package com.charizad.compiled.gamification_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeProgressResponse {
    private UUID badgeId;
    private int currentValue;
    private int requiredValue;
    private boolean completed;
    private int percentageComplete;
}
