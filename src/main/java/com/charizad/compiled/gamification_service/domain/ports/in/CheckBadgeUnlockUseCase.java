package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.request.BadgeUnlockEventRequest;

import java.util.List;
import java.util.UUID;

public interface CheckBadgeUnlockUseCase {
    /**
     * Evaluates which badges (monas) can be unlocked for a user based on the incoming event.
     * Returns the IDs of the badges that were newly awarded.
     */
    List<UUID> execute(BadgeUnlockEventRequest event);
}
