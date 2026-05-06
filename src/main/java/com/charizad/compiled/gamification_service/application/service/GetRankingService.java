package com.charizad.compiled.gamification_service.application.service;

import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRankingService implements GetRankingUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public List<RankingEntryResponse> execute(int limit) {
        List<UserGamification> topUsers = userGamificationRepository
                .findAllOptedInOrderByWeeklyXpDesc(limit);

        List<RankingEntryResponse> ranking = new ArrayList<>();
        for (int i = 0; i < topUsers.size(); i++) {
            UserGamification user = topUsers.get(i);
            ranking.add(RankingEntryResponse.builder()
                    .position(i + 1)
                    .userId(user.getUserId())
                    .weeklyXp(user.getWeeklyXp())
                    .totalBadgesEarned(user.getEarnedBadges().size())
                    .build());
        }
        return ranking;
    }
}
