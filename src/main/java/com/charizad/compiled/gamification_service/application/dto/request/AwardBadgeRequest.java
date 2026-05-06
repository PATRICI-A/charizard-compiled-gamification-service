package com.charizad.compiled.gamification_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwardBadgeRequest {

    @NotBlank(message = "El userId es obligatorio")
    private String userId;

    @NotBlank(message = "El badgeId es obligatorio")
    private String badgeId;
}
