package com.charizad.compiled.gamification_service.domain.ports.in;

public interface ToggleRankingOptInUseCase {
    /**
     * Explicitly sets the ranking opt-in status for a user.
     *
     * @param userId     the user's ID
     * @param participar true to join the ranking, false to leave
     * @return the new opt-in status
     */
    boolean execute(String userId, boolean participar);
}
