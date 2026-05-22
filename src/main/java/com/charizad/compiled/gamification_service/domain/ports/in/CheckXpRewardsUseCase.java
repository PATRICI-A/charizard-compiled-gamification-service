package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;

import java.util.List;
import java.util.UUID;

/**
 * Evaluates all active rewards against the user's current XP and unlocks
 * every reward whose threshold has been reached but not yet granted.
 * Called internally after any XP-granting operation (e.g. badge awarded).
 *
 * @return the list of newly unlocked reward IDs (empty if nothing changed)
 */
public interface CheckXpRewardsUseCase {

    List<UUID> checkAndUnlock(UserGamification user);
}
