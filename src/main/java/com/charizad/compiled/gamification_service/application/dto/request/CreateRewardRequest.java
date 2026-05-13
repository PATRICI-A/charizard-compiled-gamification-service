package com.charizad.compiled.gamification_service.application.dto.request;

import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRewardRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Type is required")
    private RewardType type;

    @Min(value = 1, message = "XP threshold must be at least 1")
    private int xpThreshold;

    private String iconUrl;
}
