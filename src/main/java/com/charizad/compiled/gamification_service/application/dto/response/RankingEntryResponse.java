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
    private String userId;
    private String displayName;
    private int monasThisWeek;
    private int totalMonas;
    private String levelName;
    /** @deprecated kept for backward compatibility; use monasThisWeek */
    @Deprecated
    private int weeklyXp;
    private int totalBadgesEarned;
}
