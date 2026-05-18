package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.domain.model.NivelCalculator;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
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
    public List<RankingEntryResponse> execute(int limit) {
        List<UserGamification> topUsers = userGamificationRepository
                .findAllOptedInOrderByWeeklyMonasDesc(limit);

        List<RankingEntryResponse> ranking = new ArrayList<>();
        for (int i = 0; i < topUsers.size(); i++) {
            UserGamification user = topUsers.get(i);
            int totalMonas = user.getTotalMonas();
            int nivel = NivelCalculator.getNivel(totalMonas);
            String displayName = userProfileClient.getDisplayName(user.getUserId())
                    .orElse(user.getUserId());

            ranking.add(RankingEntryResponse.builder()
                    .position(i + 1)
                    .userId(user.getUserId())
                    .displayName(displayName)
                    .monasThisWeek(user.getWeeklyMonas())
                    .totalMonas(totalMonas)
                    .levelName(NivelCalculator.getNivelName(nivel))
                    .weeklyXp(user.getWeeklyXp())
                    .totalBadgesEarned(user.getEarnedBadges().size())
                    .build());
        }
        return ranking;
    }
}
