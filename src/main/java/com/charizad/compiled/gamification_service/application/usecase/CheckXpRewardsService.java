package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.domain.model.Reward;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.CheckXpRewardsUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.RewardRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedReward;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Scans all active rewards and grants any whose XP threshold the user has
 * reached but hasn't received yet. Persists the updated user if anything changed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckXpRewardsService implements CheckXpRewardsUseCase {

    private final RewardRepositoryPort rewardRepository;
    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public List<UUID> checkAndUnlock(UserGamification user) {
        List<Reward> candidates = rewardRepository.findAllActive();
        List<UUID> newlyUnlocked = new ArrayList<>();

        for (Reward reward : candidates) {
            if (user.getTotalXp() >= reward.getXpThreshold()
                    && !user.hasReward(reward.getId())) {

                EarnedReward earned = EarnedReward.builder()
                        .rewardId(reward.getId())
                        .rewardName(reward.getName())
                        .rewardType(reward.getType())
                        .unlockedAt(LocalDateTime.now())
                        .xpAtUnlock(user.getTotalXp())
                        .build();

                user.unlockReward(earned);
                newlyUnlocked.add(reward.getId());

                log.info("[Rewards] Unlocked '{}' for user {} (totalXp={})",
                        reward.getName(), user.getUserId(), user.getTotalXp());
            }
        }

        if (!newlyUnlocked.isEmpty()) {
            userGamificationRepository.save(user);
        }

        return newlyUnlocked;
    }
}
