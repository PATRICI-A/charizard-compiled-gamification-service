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
    /** Null when the user has no active opt-in (RN-13.3.1). */
    private Integer position;
    private long totalParticipants;
    private int monasThisPeriod;
    private String levelName;
    private boolean rankingOptIn;
}
