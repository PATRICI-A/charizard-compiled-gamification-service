package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.request.BadgeUnlockEventRequest;

import java.util.List;

public interface CheckBadgeUnlockUseCase {
    /**
     * Evaluates which badges (monas) can be unlocked for a user based on the incoming event.
     * Returns the IDs of the badges that were newly awarded.
     */
    List<String> execute(BadgeUnlockEventRequest event);
}
