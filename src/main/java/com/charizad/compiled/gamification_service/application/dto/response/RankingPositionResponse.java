package com.charizad.compiled.gamification_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingPositionResponse {
    private String userId;
    private int posicion;
    private long totalParticipantes;
    private int monasThisWeek;
    private String levelName;
}
