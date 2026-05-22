package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.valueobjects.MonaProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedMona;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedReward;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.MonaProgressSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EarnedMonaSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EarnedRewardSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.UserGamificationDocument;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserGamificationDocumentMapper {

    public UserGamification toDomain(UserGamificationDocument doc) {
        List<EarnedMona> earned = doc.getEarnedMonas() == null ? new ArrayList<>() :
                doc.getEarnedMonas().stream()
                        .map(e -> EarnedMona.builder()
                                .monaId(e.getMonaId())
                                .monaName(e.getMonaName())
                                .earnedAt(e.getEarnedAt())
                                .xpAwarded(e.getXpAwarded())
                                .build())
                        .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        List<MonaProgress> progress = doc.getProgress() == null ? new ArrayList<>() :
                doc.getProgress().stream()
                        .map(p -> MonaProgress.builder()
                                .monaId(p.getMonaId())
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

        List<String> zones = doc.getVisitedCampusZones() == null
                ? new ArrayList<>()
                : new ArrayList<>(doc.getVisitedCampusZones());

        return UserGamification.builder()
                .id(doc.getId())
                .userId(doc.getUserId())
                .totalXp(doc.getTotalXp())
                .weeklyXp(doc.getWeeklyXp())
                .weeklyMonas(doc.getWeeklyMonas())
                .rankingOptIn(doc.isRankingOptIn())
                .earnedMonas(earned)
                .progress(progress)
                .earnedRewards(rewards)
                .visitedCampusZones(zones)
                .build();
    }

    public UserGamificationDocument toDocument(UserGamification user) {
        List<EarnedMonaSubdocument> earned = user.getEarnedMonas().stream()
                .map(e -> EarnedMonaSubdocument.builder()
                        .monaId(e.getMonaId())
                        .monaName(e.getMonaName())
                        .earnedAt(e.getEarnedAt())
                        .xpAwarded(e.getXpAwarded())
                        .build())
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        List<MonaProgressSubdocument> progress = user.getProgress().stream()
                .map(p -> MonaProgressSubdocument.builder()
                        .monaId(p.getMonaId())
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
                .weeklyMonas(user.getWeeklyMonas())
                .rankingOptIn(user.isRankingOptIn())
                .earnedMonas(earned)
                .progress(progress)
                .earnedRewards(rewards)
                .visitedCampusZones(new ArrayList<>(user.getVisitedCampusZones()))
                .build();
    }
}
