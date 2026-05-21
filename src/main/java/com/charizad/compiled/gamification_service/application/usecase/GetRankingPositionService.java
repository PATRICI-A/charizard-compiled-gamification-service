package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.domain.model.NivelCalculator;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingPositionUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRankingPositionService implements GetRankingPositionUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public RankingPositionResponse execute(String userId, RankingType type) {
        UserGamification user = userGamificationRepository.findByUserId(userId).orElse(null);

        if (user == null || !user.isRankingOptIn()) {
            return RankingPositionResponse.builder()
                    .userId(userId)
                    .position(null)
                    .rankingOptIn(false)
                    .build();
        }

        List<UserGamification> ranked = userGamificationRepository.findAllOptedInRankedFor(type);

        int position = -1;
        for (int i = 0; i < ranked.size(); i++) {
            if (ranked.get(i).getUserId().equals(userId)) {
                position = i + 1;
                break;
            }
        }

        int periodMonas = switch (type) {
            case MONTHLY -> user.getMonthlyMonas();
            case SEMESTER -> user.getSemestralMonas();
            default -> user.getWeeklyMonas();
        };
        int totalMonas = user.getTotalMonas();
        int nivel = NivelCalculator.getNivel(totalMonas);

        return RankingPositionResponse.builder()
                .userId(userId)
                .position(position)
                .totalParticipants(ranked.size())
                .monasThisPeriod(periodMonas)
                .levelName(NivelCalculator.getNivelName(nivel))
                .rankingOptIn(true)
                .build();
    }
}
