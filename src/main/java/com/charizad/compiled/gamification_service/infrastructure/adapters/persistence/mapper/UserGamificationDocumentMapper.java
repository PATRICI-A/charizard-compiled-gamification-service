package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeProgressSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EarnedBadgeSubdocument;
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

        return UserGamification.builder()
                .id(doc.getId())
                .userId(doc.getUserId())
                .totalXp(doc.getTotalXp())
                .weeklyXp(doc.getWeeklyXp())
                .rankingOptIn(doc.isRankingOptIn())
                .earnedBadges(earned)
                .progress(progress)
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

        return UserGamificationDocument.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .totalXp(user.getTotalXp())
                .weeklyXp(user.getWeeklyXp())
                .rankingOptIn(user.isRankingOptIn())
                .earnedBadges(earned)
                .progress(progress)
                .build();
    }
}
