package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.domain.model.NivelCalculator;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.infrastructure.adapters.out.feign.UserProfileClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRankingService implements GetRankingUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;
    private final UserProfileClient userProfileClient;

    @Override
    public List<RankingEntryResponse> execute(RankingType type) {
        List<UserGamification> sorted = userGamificationRepository.findAllOptedInRankedFor(type);

        List<RankingEntryResponse> ranking = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            UserGamification user = sorted.get(i);
            int periodMonas = switch (type) {
                case MONTHLY -> user.getMonthlyMonas();
                case SEMESTER -> user.getSemestralMonas();
                default -> user.getWeeklyMonas();
            };
            int totalMonas = user.getTotalMonas();
            int nivel = NivelCalculator.getNivel(totalMonas);
            String displayName = userProfileClient.getDisplayName(user.getUserId())
                    .orElse(user.getUserId());

            ranking.add(RankingEntryResponse.builder()
                    .position(i + 1)
                    .userId(user.getUserId())
                    .displayName(displayName)
                    .monasThisPeriod(periodMonas)
                    .totalMonas(totalMonas)
                    .levelName(NivelCalculator.getNivelName(nivel))
                    .type(type.name())
                    .totalBadgesEarned(user.getEarnedBadges().size())
                    .build());
        }
        return ranking;
    }
}
