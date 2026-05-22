package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.domain.model.NivelCalculator;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingPositionUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetRankingPositionService implements GetRankingPositionUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public RankingPositionResponse execute(String userId, RankingType type) {
        UserGamification user = userGamificationRepository.findByUserId(userId)
                .orElse(null);

        long totalParticipantes = userGamificationRepository.countAllOptedIn();

        if (user == null || !user.isRankingOptIn()) {
            return RankingPositionResponse.builder()
                    .userId(userId)
                    .posicion(0)
                    .totalParticipantes(totalParticipantes)
                    .monasThisPeriod(0)
                    .levelName(NivelCalculator.getNivelName(1))
                    .build();
        }

        int monasThisPeriod = switch (type) {
            case WEEKLY -> user.getWeeklyMonas();
            case MONTHLY -> user.getMonthlyMonas();
            case SEMESTER -> user.getSemesterMonas();
        };

        long usersAhead = switch (type) {
            case WEEKLY -> userGamificationRepository.countOptedInWithMoreWeeklyMonasThan(monasThisPeriod);
            case MONTHLY -> userGamificationRepository.countOptedInWithMoreMonthlyMonasThan(monasThisPeriod);
            case SEMESTER -> userGamificationRepository.countOptedInWithMoreSemesterMonasThan(monasThisPeriod);
        };
        
        int posicion = (int) usersAhead + 1;

        int totalXp = user.getTotalXp();
        int nivel = NivelCalculator.getNivel(totalXp);

        return RankingPositionResponse.builder()
                .userId(userId)
                .posicion(posicion)
                .totalParticipantes(totalParticipantes)
                .monasThisPeriod(monasThisPeriod)
                .levelName(NivelCalculator.getNivelName(nivel))
                .build();
    }
}
