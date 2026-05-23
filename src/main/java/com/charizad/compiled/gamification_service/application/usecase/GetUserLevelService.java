package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.UserLevelResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.NivelCalculator;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserLevelUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserLevelService implements GetUserLevelUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public UserLevelResponse execute(String userId) {
        UserGamification user = userGamificationRepository.findByUserId(userId)
                .orElseThrow(() -> new UserGamificationNotFoundException(userId));

        int totalXp = user.getTotalXp();
        int nivel = NivelCalculator.getNivel(totalXp);

        return UserLevelResponse.builder()
                .userId(userId)
                .currentLevel(nivel)
                .levelName(NivelCalculator.getNivelName(nivel))
                .totalMonasEarned(user.getTotalMonas())
                .totalXP(totalXp)
                .xpForNextLevel(NivelCalculator.getXpParaSiguienteNivel(totalXp))
                .xpRemaining(NivelCalculator.getXpRestante(totalXp))
                .progressPercentage(NivelCalculator.getProgressPercentage(totalXp))
                .isMaxLevel(NivelCalculator.isMaxLevel(totalXp))
                .currentReward(NivelCalculator.getReward(nivel))
                .build();
    }
}
