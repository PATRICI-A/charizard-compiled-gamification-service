package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.CreateRewardRequest;
import com.charizad.compiled.gamification_service.application.dto.response.RewardResponse;
import com.charizad.compiled.gamification_service.domain.model.Reward;
import com.charizad.compiled.gamification_service.domain.ports.in.CreateRewardUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.RewardRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateRewardService implements CreateRewardUseCase {

    private final RewardRepositoryPort rewardRepository;

    @Override
    public RewardResponse execute(CreateRewardRequest request) {
        Reward reward = Reward.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .xpThreshold(request.getXpThreshold())
                .iconUrl(request.getIconUrl())
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        Reward saved = rewardRepository.save(reward);
        return toResponse(saved);
    }

    private RewardResponse toResponse(Reward reward) {
        return RewardResponse.builder()
                .id(reward.getId())
                .name(reward.getName())
                .description(reward.getDescription())
                .type(reward.getType())
                .xpThreshold(reward.getXpThreshold())
                .iconUrl(reward.getIconUrl())
                .active(reward.isActive())
                .createdAt(reward.getCreatedAt())
                .build();
    }
}
