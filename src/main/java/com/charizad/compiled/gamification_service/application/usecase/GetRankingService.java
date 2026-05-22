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
    public List<RankingEntryResponse> execute(RankingType type, int limit) {
        List<UserGamification> topUsers = switch (type) {
            case WEEKLY -> userGamificationRepository.findAllOptedInOrderByWeeklyMonasDesc(limit);
            case MONTHLY -> userGamificationRepository.findAllOptedInOrderByMonthlyMonasDesc(limit);
            case SEMESTER -> userGamificationRepository.findAllOptedInOrderBySemesterMonasDesc(limit);
        };

        List<RankingEntryResponse> ranking = new ArrayList<>();
        for (int i = 0; i < topUsers.size(); i++) {
            UserGamification user = topUsers.get(i);
            int totalXp = user.getTotalXp();
            int nivel = NivelCalculator.getNivel(totalXp);
            String displayName = userProfileClient.getDisplayName(user.getUserId())
                    .orElse(user.getUserId());

            int monasThisPeriod = switch (type) {
                case WEEKLY -> user.getWeeklyMonas();
                case MONTHLY -> user.getMonthlyMonas();
                case SEMESTER -> user.getSemesterMonas();
            };

            ranking.add(RankingEntryResponse.builder()
                    .position(i + 1)
                    .userId(user.getUserId())
                    .displayName(displayName)
                    .monasThisPeriod(monasThisPeriod)
                    .totalMonas(user.getTotalMonas())
                    .levelName(NivelCalculator.getNivelName(nivel))
                    .build());
        }
        return ranking;
    }
}
