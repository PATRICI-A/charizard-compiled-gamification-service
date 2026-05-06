package com.charizad.compiled.gamification_service.domain.model;

import com.charizad.compiled.gamification_service.domain.exceptions.BadgeAlreadyEarnedException;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserGamification {

    private final String id;
    private final String userId;
    private int totalXp;
    private int weeklyXp;
    private boolean rankingOptIn;
    private final List<EarnedBadge> earnedBadges;
    private final List<BadgeProgress> progress;

    public boolean hasBadge(String badgeId) {
        return earnedBadges.stream().anyMatch(b -> b.getBadgeId().equals(badgeId));
    }

    public void awardBadge(EarnedBadge earned) {
        if (hasBadge(earned.getBadgeId())) {
            throw new BadgeAlreadyEarnedException(userId, earned.getBadgeId());
        }
        earnedBadges.add(earned);
        totalXp += earned.getXpAwarded();
        weeklyXp += earned.getXpAwarded();
    }

    public void resetWeeklyXp() {
        weeklyXp = 0;
    }

    public void toggleRankingOptIn() {
        rankingOptIn = !rankingOptIn;
    }

    public List<EarnedBadge> getEarnedBadges() {
        return Collections.unmodifiableList(earnedBadges);
    }

    public List<BadgeProgress> getProgress() {
        return Collections.unmodifiableList(progress);
    }

    // Factory: new user
    public static UserGamification newUser(String userId) {
        return UserGamification.builder()
                .userId(userId)
                .totalXp(0)
                .weeklyXp(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();
    }
}
