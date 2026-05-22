package com.charizad.compiled.gamification_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwardBadgeRequest {

    @NotBlank(message = "El userId es obligatorio")
    private String userId;

    @NotNull(message = "El badgeId es obligatorio")
    private UUID badgeId;
}
