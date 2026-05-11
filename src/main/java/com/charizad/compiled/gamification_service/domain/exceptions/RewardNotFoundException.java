package com.charizad.compiled.gamification_service.domain.exceptions;

public class RewardNotFoundException extends RuntimeException {

    public RewardNotFoundException(String rewardId) {
        super("Reward not found: " + rewardId);
    }
}
