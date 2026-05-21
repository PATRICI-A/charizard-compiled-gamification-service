package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingOptInResponse;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.ToggleRankingOptInUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ToggleRankingOptInService implements ToggleRankingOptInUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public RankingOptInResponse execute(String userId, boolean participe) {
        UserGamification user = userGamificationRepository.findByUserId(userId)
                .orElseGet(() -> UserGamification.newUser(userId));

        user.setRankingOptIn(participe);
        userGamificationRepository.save(user);

        return RankingOptInResponse.builder()
                .studentId(userId)
                .rankingOptIn(user.isRankingOptIn())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
