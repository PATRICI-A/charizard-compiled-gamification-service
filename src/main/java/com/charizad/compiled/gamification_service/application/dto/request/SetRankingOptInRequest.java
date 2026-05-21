package com.charizad.compiled.gamification_service.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SetRankingOptInRequest {

    @NotNull(message = "Field 'participe' is required")
    private Boolean participe;
}
