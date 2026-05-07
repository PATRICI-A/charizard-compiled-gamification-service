package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.ToggleRankingOptInUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ToggleRankingOptInService implements ToggleRankingOptInUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public boolean execute(String userId) {
        UserGamification user = userGamificationRepository.findByUserId(userId)
                .orElseGet(() -> UserGamification.newUser(userId));

        user.toggleRankingOptIn();
        userGamificationRepository.save(user);
        return user.isRankingOptIn();
    }
}
