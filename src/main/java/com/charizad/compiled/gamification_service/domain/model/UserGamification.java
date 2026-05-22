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
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserGamification {

    private final UUID id;
    private final String userId;
    private int totalXp;
    private int weeklyXp;
    private int weeklyMonas;
    private int monthlyMonas;
    private int semestralMonas;
    private boolean rankingOptIn;
    private final List<EarnedBadge> earnedBadges;
    private final List<BadgeProgress> progress;
    private final List<EarnedReward> earnedRewards;
    private final List<String> visitedCampusZones;

    public boolean hasBadge(UUID badgeId) {
        return earnedBadges.stream().anyMatch(b -> b.getBadgeId().equals(badgeId));
    }

    public boolean hasReward(UUID rewardId) {
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
        monthlyMonas++;
        semestralMonas++;
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

    public void resetMonthlyMonas() {
        monthlyMonas = 0;
    }

    public void resetSemestralMonas() {
        semestralMonas = 0;
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

    public List<String> getVisitedCampusZones() {
        return visitedCampusZones == null ? List.of() : Collections.unmodifiableList(visitedCampusZones);
    }

    /** Adds zone if not already visited. Returns true if it was a new zone. */
    public boolean visitZone(String campusZone) {
        if (visitedCampusZones == null || visitedCampusZones.contains(campusZone)) return false;
        visitedCampusZones.add(campusZone);
        return true;
    }

    /** Upserts the BadgeProgress entry for the given badgeId. */
    public void updateProgress(UUID badgeId, int currentValue, int requiredValue) {
        progress.removeIf(p -> p.getBadgeId().equals(badgeId));
        progress.add(BadgeProgress.builder()
                .badgeId(badgeId).currentValue(currentValue)
                .requiredValue(requiredValue)
                .completed(currentValue >= requiredValue)
                .build());
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
                .monthlyMonas(0)
                .semestralMonas(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .visitedCampusZones(new ArrayList<>())
                .build();
    }
}
