package com.charizad.compiled.gamification_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EarnedMonaResponse {
    private String monaId;
    private String monaName;
    private LocalDateTime earnedAt;
    private int xpAwarded;
}
