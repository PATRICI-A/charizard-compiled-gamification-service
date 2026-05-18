package com.charizad.compiled.gamification_service.application.mapper;

import com.charizad.compiled.gamification_service.application.dto.response.BadgeProgressResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.model.NivelCalculator;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
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
                .totalBadgesEarned(user.getEarnedBadges().size())
                .totalRewardsUnlocked(user.getEarnedRewards().size())
                .build();
    }

    public EarnedBadgeResponse toEarnedBadgeResponse(EarnedBadge earned) {
        return EarnedBadgeResponse.builder()
                .badgeId(earned.getBadgeId())
                .badgeName(earned.getBadgeName())
                .earnedAt(earned.getEarnedAt())
                .xpAwarded(earned.getXpAwarded())
                .build();
    }

    public BadgeProgressResponse toProgressResponse(BadgeProgress progress) {
        int percentage = progress.getRequiredValue() > 0
                ? (int) ((progress.getCurrentValue() * 100.0) / progress.getRequiredValue())
                : 0;
        return BadgeProgressResponse.builder()
                .badgeId(progress.getBadgeId())
                .currentValue(progress.getCurrentValue())
                .requiredValue(progress.getRequiredValue())
                .completed(progress.isCompleted())
                .percentageComplete(Math.min(percentage, 100))
                .build();
    }
}
