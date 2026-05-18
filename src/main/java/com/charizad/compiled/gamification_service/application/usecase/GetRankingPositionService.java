package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.domain.model.NivelCalculator;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingPositionUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetRankingPositionService implements GetRankingPositionUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public RankingPositionResponse execute(String userId) {
        UserGamification user = userGamificationRepository.findByUserId(userId)
                .orElse(null);

        long totalParticipantes = userGamificationRepository.countAllOptedIn();

        if (user == null || !user.isRankingOptIn()) {
            return RankingPositionResponse.builder()
                    .userId(userId)
                    .posicion(0)
                    .totalParticipantes(totalParticipantes)
                    .monasThisWeek(0)
                    .levelName(NivelCalculator.getNivelName(1))
                    .build();
        }

        long usersAhead = userGamificationRepository.countOptedInWithMoreMonasThan(user.getWeeklyMonas());
        int posicion = (int) usersAhead + 1;

        int totalMonas = user.getTotalMonas();
        int nivel = NivelCalculator.getNivel(totalMonas);

        return RankingPositionResponse.builder()
                .userId(userId)
                .posicion(posicion)
                .totalParticipantes(totalParticipantes)
                .monasThisWeek(user.getWeeklyMonas())
                .levelName(NivelCalculator.getNivelName(nivel))
                .build();
    }
}
