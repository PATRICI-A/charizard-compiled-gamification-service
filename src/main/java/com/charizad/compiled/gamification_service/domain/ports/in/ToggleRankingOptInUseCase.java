package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.response.RankingOptInResponse;

public interface ToggleRankingOptInUseCase {
    /**
     * Explicitly sets the ranking opt-in status for a user.
     *
     * @param userId   the user's ID
     * @param participe true to join the ranking, false to leave
     * @return response with studentId, rankingOptIn status and updatedAt timestamp
     */
    RankingOptInResponse execute(String userId, boolean participe);
}
