package com.charizad.compiled.gamification_service.application.dto.response;

import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardResponse {

    private String id;
    private String name;
    private String description;
    private RewardType type;
    private int xpThreshold;
    private String iconUrl;
    private boolean active;
    private LocalDateTime createdAt;
}
