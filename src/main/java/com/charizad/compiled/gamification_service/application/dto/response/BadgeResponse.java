package com.charizad.compiled.gamification_service.application.dto.response;

import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeResponse {
    private UUID id;
    private String name;
    private String description;
    private BadgeCategory category;
    private int xpReward;
    private String iconUrl;
    private LocalDateTime createdAt;
}
