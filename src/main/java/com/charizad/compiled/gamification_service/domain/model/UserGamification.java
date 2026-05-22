package com.charizad.compiled.gamification_service.domain.model;

import com.charizad.compiled.gamification_service.domain.exceptions.MonaAlreadyEarnedException;
import com.charizad.compiled.gamification_service.domain.valueobjects.MonaProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedMona;
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
    private int monthlyXp;
    private int semesterXp;
    private int weeklyMonas;
    private int monthlyMonas;
    private int semesterMonas;
    private boolean rankingOptIn;
    private final List<EarnedMona> earnedMonas;
    private final List<MonaProgress> progress;
    private final List<EarnedReward> earnedRewards;
    private final List<String> visitedCampusZones;

    public boolean hasMona(String monaId) {
        return earnedMonas.stream().anyMatch(b -> b.getMonaId().equals(monaId));
    }

    public boolean hasReward(String rewardId) {
        return earnedRewards.stream().anyMatch(r -> r.getRewardId().equals(rewardId));
    }

    public void awardMona(EarnedMona earned) {
        if (hasMona(earned.getMonaId())) {
            throw new MonaAlreadyEarnedException(userId, earned.getMonaId());
        }
        earnedMonas.add(earned);
        totalXp += earned.getXpAwarded();
        weeklyXp += earned.getXpAwarded();
        monthlyXp += earned.getXpAwarded();
        semesterXp += earned.getXpAwarded();
        weeklyMonas++;
        monthlyMonas++;
        semesterMonas++;
    }

    public void unlockReward(EarnedReward earned) {
        earnedRewards.add(earned);
    }

    public void resetWeeklyStats() {
        weeklyXp = 0;
        weeklyMonas = 0;
    }

    public void resetMonthlyStats() {
        monthlyXp = 0;
        monthlyMonas = 0;
    }

    public void resetSemesterStats() {
        semesterXp = 0;
        semesterMonas = 0;
    }

    public void toggleRankingOptIn() {
        rankingOptIn = !rankingOptIn;
    }

    public void setRankingOptIn(boolean participar) {
        rankingOptIn = participar;
    }

    public List<EarnedMona> getEarnedMonas() {
        return Collections.unmodifiableList(earnedMonas);
    }

    public List<MonaProgress> getProgress() {
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

    /** Upserts the MonaProgress entry for the given monaId. */
    public void updateProgress(String monaId, int currentValue, int requiredValue) {
        progress.removeIf(p -> p.getMonaId().equals(monaId));
        progress.add(MonaProgress.builder()
                .monaId(monaId).currentValue(currentValue)
                .requiredValue(requiredValue)
                .completed(currentValue >= requiredValue)
                .build());
    }

    public int getTotalMonas() {
        return earnedMonas.size();
    }

    public static UserGamification newUser(String userId) {
        return UserGamification.builder()
                .userId(userId)
                .totalXp(0)
                .weeklyXp(0)
                .monthlyXp(0)
                .semesterXp(0)
                .weeklyMonas(0)
                .monthlyMonas(0)
                .semesterMonas(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .visitedCampusZones(new ArrayList<>())
                .build();
    }
}
