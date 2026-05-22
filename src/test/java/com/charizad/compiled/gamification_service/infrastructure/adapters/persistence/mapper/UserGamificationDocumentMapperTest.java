package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import com.charizad.compiled.gamification_service.domain.valueobjects.MonaProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedMona;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedReward;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.MonaProgressSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EarnedMonaSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EarnedRewardSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.UserGamificationDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserGamificationDocumentMapperTest {

    private final UserGamificationDocumentMapper mapper = new UserGamificationDocumentMapper();

    @Test
    @DisplayName("toDomain mapea UserGamificationDocument a UserGamification con listas pobladas")
    void toDomain_shouldMapDocumentWithLists() {
        LocalDateTime now = LocalDateTime.now();

        EarnedMonaSubdocument earnedSub = EarnedMonaSubdocument.builder()
                .monaId("Mona-001")
                .monaName("Primer Parche")
                .earnedAt(now)
                .xpAwarded(100)
                .build();

        MonaProgressSubdocument progressSub = MonaProgressSubdocument.builder()
                .monaId("Mona-002")
                .currentValue(50)
                .requiredValue(100)
                .completed(false)
                .build();

        UserGamificationDocument doc = UserGamificationDocument.builder()
                .id("ug-001")
                .userId("user-001")
                .totalXp(500)
                .weeklyXp(200)
                .rankingOptIn(true)
                .earnedMonas(List.of(earnedSub))
                .progress(List.of(progressSub))
                .build();

        UserGamification result = mapper.toDomain(doc);

        assertThat(result.getId()).isEqualTo("ug-001");
        assertThat(result.getUserId()).isEqualTo("user-001");
        assertThat(result.getTotalXp()).isEqualTo(500);
        assertThat(result.getWeeklyXp()).isEqualTo(200);
        assertThat(result.isRankingOptIn()).isTrue();
        assertThat(result.getEarnedMonas()).hasSize(1);
        assertThat(result.getEarnedMonas().get(0).getMonaId()).isEqualTo("Mona-001");
        assertThat(result.getProgress()).hasSize(1);
        assertThat(result.getProgress().get(0).getMonaId()).isEqualTo("Mona-002");
    }

    @Test
    @DisplayName("toDomain mapea UserGamificationDocument con earnedRewards poblados")
    void toDomain_shouldMapDocumentWithEarnedRewards() {
        LocalDateTime now = LocalDateTime.now();

        EarnedRewardSubdocument rewardSub = EarnedRewardSubdocument.builder()
                .rewardId("reward-001")
                .rewardName("Gold Title")
                .rewardType(RewardType.TITLE)
                .unlockedAt(now)
                .xpAtUnlock(1000)
                .build();

        EarnedMonaSubdocument earnedSub = EarnedMonaSubdocument.builder()
                .monaId("Mona-001")
                .monaName("Primer Parche")
                .earnedAt(now)
                .xpAwarded(100)
                .build();

        UserGamificationDocument doc = UserGamificationDocument.builder()
                .id("ug-010")
                .userId("user-010")
                .totalXp(1500)
                .weeklyXp(300)
                .rankingOptIn(true)
                .earnedMonas(List.of(earnedSub))
                .progress(new ArrayList<>())
                .earnedRewards(List.of(rewardSub))
                .build();

        UserGamification result = mapper.toDomain(doc);

        assertThat(result.getId()).isEqualTo("ug-010");
        assertThat(result.getUserId()).isEqualTo("user-010");
        assertThat(result.getTotalXp()).isEqualTo(1500);
        assertThat(result.getWeeklyXp()).isEqualTo(300);
        assertThat(result.isRankingOptIn()).isTrue();
        assertThat(result.getEarnedRewards()).hasSize(1);
        assertThat(result.getEarnedRewards().get(0).getRewardId()).isEqualTo("reward-001");
        assertThat(result.getEarnedRewards().get(0).getRewardName()).isEqualTo("Gold Title");
        assertThat(result.getEarnedRewards().get(0).getRewardType()).isEqualTo(RewardType.TITLE);
        assertThat(result.getEarnedRewards().get(0).getUnlockedAt()).isEqualTo(now);
        assertThat(result.getEarnedRewards().get(0).getXpAtUnlock()).isEqualTo(1000);
    }

    @Test
    @DisplayName("toDomain maneja listas nulas como listas vacías")
    void toDomain_shouldHandleNullLists() {
        UserGamificationDocument doc = UserGamificationDocument.builder()
                .id("ug-002")
                .userId("user-002")
                .totalXp(0)
                .weeklyXp(0)
                .rankingOptIn(false)
                .earnedMonas(null)
                .progress(null)
                .build();

        UserGamification result = mapper.toDomain(doc);

        assertThat(result.getEarnedMonas()).isEmpty();
        assertThat(result.getProgress()).isEmpty();
    }

    @Test
    @DisplayName("toDocument mapea UserGamification a UserGamificationDocument")
    void toDocument_shouldMapDomainToDocument() {
        LocalDateTime now = LocalDateTime.now();

        EarnedReward reward = EarnedReward.builder()
                .rewardId("reward-001")
                .rewardName("Gold Title")
                .rewardType(RewardType.TITLE)
                .unlockedAt(now)
                .xpAtUnlock(1000)
                .build();

        EarnedMona earned = EarnedMona.builder()
                .monaId("Mona-001")
                .monaName("Primer Parche")
                .earnedAt(now)
                .xpAwarded(100)
                .build();

        MonaProgress progress = MonaProgress.builder()
                .monaId("Mona-002")
                .currentValue(50)
                .requiredValue(100)
                .completed(false)
                .build();

        UserGamification user = UserGamification.builder()
                .id("ug-001")
                .userId("user-001")
                .totalXp(500)
                .weeklyXp(200)
                .rankingOptIn(true)
                .earnedMonas(List.of(earned))
                .progress(List.of(progress))
                .earnedRewards(List.of(reward))
                .build();

        UserGamificationDocument result = mapper.toDocument(user);

        assertThat(result.getId()).isEqualTo("ug-001");
        assertThat(result.getUserId()).isEqualTo("user-001");
        assertThat(result.getTotalXp()).isEqualTo(500);
        assertThat(result.getWeeklyXp()).isEqualTo(200);
        assertThat(result.isRankingOptIn()).isTrue();
        assertThat(result.getEarnedMonas()).hasSize(1);
        assertThat(result.getEarnedMonas().get(0).getMonaId()).isEqualTo("Mona-001");
        assertThat(result.getProgress()).hasSize(1);
        assertThat(result.getProgress().get(0).getMonaId()).isEqualTo("Mona-002");
        assertThat(result.getEarnedRewards()).hasSize(1);
        assertThat(result.getEarnedRewards().get(0).getRewardId()).isEqualTo("reward-001");
        assertThat(result.getEarnedRewards().get(0).getRewardName()).isEqualTo("Gold Title");
        assertThat(result.getEarnedRewards().get(0).getRewardType()).isEqualTo(RewardType.TITLE);
        assertThat(result.getEarnedRewards().get(0).getUnlockedAt()).isEqualTo(now);
        assertThat(result.getEarnedRewards().get(0).getXpAtUnlock()).isEqualTo(1000);
    }

    @Test
    @DisplayName("toDocument mapea usuario sin insignias ni progreso")
    void toDocument_shouldMapEmptyLists() {
        UserGamification user = UserGamification.builder()
                .id("ug-002")
                .userId("user-002")
                .totalXp(0)
                .weeklyXp(0)
                .rankingOptIn(false)
                .earnedMonas(List.of())
                .progress(List.of())
                .earnedRewards(new ArrayList<>())
                .build();

        UserGamificationDocument result = mapper.toDocument(user);

        assertThat(result.getEarnedMonas()).isEmpty();
        assertThat(result.getProgress()).isEmpty();
    }

    @Test
    @DisplayName("Mapeo bidireccional es consistente")
    void shouldBeBidirectionalConsistent() {
        LocalDateTime now = LocalDateTime.now();

        EarnedRewardSubdocument rewardSub = EarnedRewardSubdocument.builder()
                .rewardId("reward-001")
                .rewardName("Gold Title")
                .rewardType(RewardType.TITLE)
                .unlockedAt(now)
                .xpAtUnlock(1000)
                .build();

        EarnedMonaSubdocument earnedSub = EarnedMonaSubdocument.builder()
                .monaId("Mona-001")
                .monaName("Test")
                .earnedAt(now)
                .xpAwarded(100)
                .build();

        MonaProgressSubdocument progressSub = MonaProgressSubdocument.builder()
                .monaId("Mona-002")
                .currentValue(30)
                .requiredValue(100)
                .completed(false)
                .build();

        UserGamificationDocument original = UserGamificationDocument.builder()
                .id("ug-003")
                .userId("user-003")
                .totalXp(300)
                .weeklyXp(100)
                .rankingOptIn(true)
                .earnedMonas(List.of(earnedSub))
                .progress(List.of(progressSub))
                .earnedRewards(List.of(rewardSub))
                .build();

        UserGamification domain = mapper.toDomain(original);
        UserGamificationDocument result = mapper.toDocument(domain);

        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getUserId()).isEqualTo(original.getUserId());
        assertThat(result.getTotalXp()).isEqualTo(original.getTotalXp());
        assertThat(result.getWeeklyXp()).isEqualTo(original.getWeeklyXp());
        assertThat(result.isRankingOptIn()).isEqualTo(original.isRankingOptIn());
        assertThat(result.getEarnedMonas()).hasSize(original.getEarnedMonas().size());
        assertThat(result.getProgress()).hasSize(original.getProgress().size());
        assertThat(result.getEarnedRewards()).hasSize(original.getEarnedRewards().size());
    }
}
