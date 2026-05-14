package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedReward;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeProgressSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EarnedBadgeSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EarnedRewardSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.UserGamificationDocument;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserGamificationDocumentMapper {

    public UserGamification toDomain(UserGamificationDocument doc) {
        List<EarnedBadge> earned = doc.getEarnedBadges() == null ? new ArrayList<>() :
                doc.getEarnedBadges().stream()
                        .map(e -> EarnedBadge.builder()
                                .badgeId(e.getBadgeId())
                                .badgeName(e.getBadgeName())
                                .earnedAt(e.getEarnedAt())
                                .xpAwarded(e.getXpAwarded())
                                .build())
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        List<BadgeProgress> progress = doc.getProgress() == null ? new ArrayList<>() :
                doc.getProgress().stream()
                        .map(p -> BadgeProgress.builder()
                                .badgeId(p.getBadgeId())
                                .currentValue(p.getCurrentValue())
                                .requiredValue(p.getRequiredValue())
                                .completed(p.isCompleted())
                                .build())
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        List<EarnedReward> rewards = doc.getEarnedRewards() == null ? new ArrayList<>() :
                doc.getEarnedRewards().stream()
                        .map(r -> EarnedReward.builder()
                                .rewardId(r.getRewardId())
                                .rewardName(r.getRewardName())
                                .rewardType(r.getRewardType())
                                .unlockedAt(r.getUnlockedAt())
                                .xpAtUnlock(r.getXpAtUnlock())
                                .build())
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        return UserGamification.builder()
                .id(doc.getId())
                .userId(doc.getUserId())
                .totalXp(doc.getTotalXp())
                .weeklyXp(doc.getWeeklyXp())
                .rankingOptIn(doc.isRankingOptIn())
                .earnedBadges(earned)
                .progress(progress)
                .earnedRewards(rewards)
                .build();
    }

    public UserGamificationDocument toDocument(UserGamification user) {
        List<EarnedBadgeSubdocument> earned = user.getEarnedBadges().stream()
                .map(e -> EarnedBadgeSubdocument.builder()
                        .badgeId(e.getBadgeId())
                        .badgeName(e.getBadgeName())
                        .earnedAt(e.getEarnedAt())
                        .xpAwarded(e.getXpAwarded())
                        .build())
                .toList();

        List<BadgeProgressSubdocument> progress = user.getProgress().stream()
                .map(p -> BadgeProgressSubdocument.builder()
                        .badgeId(p.getBadgeId())
                        .currentValue(p.getCurrentValue())
                        .requiredValue(p.getRequiredValue())
                        .completed(p.isCompleted())
                        .build())
                .toList();

        List<EarnedRewardSubdocument> rewards = user.getEarnedRewards().stream()
                .map(r -> EarnedRewardSubdocument.builder()
                        .rewardId(r.getRewardId())
                        .rewardName(r.getRewardName())
                        .rewardType(r.getRewardType())
                        .unlockedAt(r.getUnlockedAt())
                        .xpAtUnlock(r.getXpAtUnlock())
                        .build())
                .toList();

        return UserGamificationDocument.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .totalXp(user.getTotalXp())
                .weeklyXp(user.getWeeklyXp())
                .rankingOptIn(user.isRankingOptIn())
                .earnedBadges(earned)
                .progress(progress)
                .earnedRewards(rewards)
                .build();
    }
}
