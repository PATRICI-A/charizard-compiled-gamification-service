package com.charizad.compiled.gamification_service.application.dto.event;

import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AchievementUnlockedEventDto {

    private String userId;
    private String monaId;
    private String monaName;
    private String monaDescription;
    private BadgeCategory monaCategory;
    private String iconUrl;
    private int xpAwarded;
    private LocalDateTime occurredAt;
}
