package com.charizad.compiled.gamification_service.domain.ports.in;

public interface ToggleRankingOptInUseCase {
    boolean execute(String userId);
}
