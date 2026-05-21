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
public class RankingOptInResponse {
    private String studentId;
    private boolean rankingOptIn;
    private LocalDateTime updatedAt;
}
