package com.charizad.compiled.gamification_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingEntryResponse {
    private int position;
    private String studentId;
    private String displayName;
    private int monasThisPeriod;
    private int totalMonas;
    private String levelName;
}
