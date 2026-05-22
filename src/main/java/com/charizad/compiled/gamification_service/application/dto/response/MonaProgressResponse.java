package com.charizad.compiled.gamification_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonaProgressResponse {
    private String monaId;
    private int currentValue;
    private int requiredValue;
    private boolean completed;
    private int percentageComplete;
}
