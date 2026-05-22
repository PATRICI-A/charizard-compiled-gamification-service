package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.request.MonaUnlockEventRequest;

import java.util.List;

public interface CheckMonaUnlockUseCase {
    /**
     * Evaluates which Monas (monas) can be unlocked for a user based on the incoming event.
     * Returns the IDs of the Monas that were newly awarded.
     */
    List<String> execute(MonaUnlockEventRequest event);
}
