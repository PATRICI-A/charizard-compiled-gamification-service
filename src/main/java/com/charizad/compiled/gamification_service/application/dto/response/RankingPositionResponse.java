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
    private String userId;
    private Integer position;
    private int monasThisPeriod;
    private boolean rankingOptIn;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private RankingType rankingType;
    private String levelName;
}
