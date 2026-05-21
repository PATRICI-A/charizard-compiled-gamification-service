package com.charizad.compiled.gamification_service.application.dto.response;

import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingPositionResponse {
    /** Null when the user has no active opt-in (RN-13.3.1). */
    private Integer position;
    private int monasThisPeriod;
    private boolean rankingOptIn;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private RankingType rankingType;
}
