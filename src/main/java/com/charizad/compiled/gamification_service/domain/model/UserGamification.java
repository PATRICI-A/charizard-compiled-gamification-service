package com.charizad.compiled.gamification_service.domain.model;

import com.charizad.compiled.gamification_service.domain.exceptions.BadgeAlreadyEarnedException;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedReward;
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
    private int weeklyMonas;
    private boolean rankingOptIn;
    private final List<EarnedBadge> earnedBadges;
    private final List<BadgeProgress> progress;
    private final List<EarnedReward> earnedRewards;

    public boolean hasBadge(String badgeId) {
        return earnedBadges.stream().anyMatch(b -> b.getBadgeId().equals(badgeId));
    }

    public boolean hasReward(String rewardId) {
        return earnedRewards.stream().anyMatch(r -> r.getRewardId().equals(rewardId));
    }

    public void awardBadge(EarnedBadge earned) {
        if (hasBadge(earned.getBadgeId())) {
            throw new BadgeAlreadyEarnedException(userId, earned.getBadgeId());
        }
        earnedBadges.add(earned);
        totalXp += earned.getXpAwarded();
        weeklyXp += earned.getXpAwarded();
        weeklyMonas++;
    }

    public void unlockReward(EarnedReward earned) {
        earnedRewards.add(earned);
    }

    public void resetWeeklyXp() {
        weeklyXp = 0;
    }

    public void resetWeeklyMonas() {
        weeklyMonas = 0;
    }

    public void toggleRankingOptIn() {
        rankingOptIn = !rankingOptIn;
    }

    public void setRankingOptIn(boolean participar) {
        rankingOptIn = participar;
    }

    public List<EarnedBadge> getEarnedBadges() {
        return Collections.unmodifiableList(earnedBadges);
    }

    public List<BadgeProgress> getProgress() {
        return Collections.unmodifiableList(progress);
    }

    public List<EarnedReward> getEarnedRewards() {
        return Collections.unmodifiableList(earnedRewards);
    }

    public int getTotalMonas() {
        return earnedBadges.size();
    }

    public static UserGamification newUser(String userId) {
        return UserGamification.builder()
                .userId(userId)
                .totalXp(0)
                .weeklyXp(0)
                .weeklyMonas(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();
    }
}
