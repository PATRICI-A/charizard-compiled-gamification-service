package com.charizad.compiled.gamification_service.application.mapper;

import com.charizad.compiled.gamification_service.application.dto.response.MonaProgressResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.model.NivelCalculator;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.valueobjects.MonaProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedMona;
import org.springframework.stereotype.Component;

@Component
public class UserGamificationMapper {

    public UserStatsResponse toStatsResponse(UserGamification user) {
        int totalMonas = user.getTotalMonas();
        int nivel = NivelCalculator.getNivel(totalMonas);
        return UserStatsResponse.builder()
                .userId(user.getUserId())
                .totalXp(user.getTotalXp())
                .weeklyXp(user.getWeeklyXp())
                .weeklyMonas(user.getWeeklyMonas())
                .totalMonas(totalMonas)
                .nivel(nivel)
                .levelName(NivelCalculator.getNivelName(nivel))
                .rankingOptIn(user.isRankingOptIn())
                .totalMonasEarned(user.getEarnedMonas().size())
                .totalRewardsUnlocked(user.getEarnedRewards().size())
                .build();
    }

    public EarnedMonaResponse toEarnedMonaResponse(EarnedMona earned) {
        return EarnedMonaResponse.builder()
                .monaId(earned.getMonaId())
                .monaName(earned.getMonaName())
                .earnedAt(earned.getEarnedAt())
                .xpAwarded(earned.getXpAwarded())
                .build();
    }

    public MonaProgressResponse toProgressResponse(MonaProgress progress) {
        int percentage = progress.getRequiredValue() > 0
                ? (int) ((progress.getCurrentValue() * 100.0) / progress.getRequiredValue())
                : 0;
        return MonaProgressResponse.builder()
                .monaId(progress.getMonaId())
                .currentValue(progress.getCurrentValue())
                .requiredValue(progress.getRequiredValue())
                .completed(progress.isCompleted())
                .percentageComplete(Math.min(percentage, 100))
                .build();
    }
}
