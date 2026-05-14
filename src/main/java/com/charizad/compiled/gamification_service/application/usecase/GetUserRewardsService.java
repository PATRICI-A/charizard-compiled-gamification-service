package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedRewardResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserRewardsUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedReward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserRewardsService implements GetUserRewardsUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public List<EarnedRewardResponse> execute(String userId) {
        UserGamification user = userGamificationRepository.findByUserId(userId)
                .orElseThrow(() -> new UserGamificationNotFoundException(userId));

        return user.getEarnedRewards().stream()
                .map(this::toResponse)
                .toList();
    }

    private EarnedRewardResponse toResponse(EarnedReward earned) {
        return EarnedRewardResponse.builder()
                .rewardId(earned.getRewardId())
                .rewardName(earned.getRewardName())
                .rewardType(earned.getRewardType())
                .unlockedAt(earned.getUnlockedAt())
                .xpAtUnlock(earned.getXpAtUnlock())
                .build();
    }
}
