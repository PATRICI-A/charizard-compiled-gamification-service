package com.charizad.compiled.gamification_service.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class EarnedMona {

    private final String monaId;
    private final String monaName;
    private final LocalDateTime earnedAt;
    private final int xpAwarded;
}
