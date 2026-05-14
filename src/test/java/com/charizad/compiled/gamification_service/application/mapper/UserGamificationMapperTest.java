package com.charizad.compiled.gamification_service.application.mapper;

import com.charizad.compiled.gamification_service.application.dto.response.BadgeProgressResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserGamificationMapperTest {

    private final UserGamificationMapper mapper = new UserGamificationMapper();

    @Test
    @DisplayName("toStatsResponse mapea UserGamification a UserStatsResponse")
    void toStatsResponse_shouldMapCorrectly() {
        List<EarnedBadge> badges = List.of(
                EarnedBadge.builder().badgeId("b1").build(),
                EarnedBadge.builder().badgeId("b2").build(),
                EarnedBadge.builder().badgeId("b3").build()
        );

        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(500)
                .weeklyXp(200)
                .rankingOptIn(true)
                .earnedBadges(badges)
                .progress(new ArrayList<>())
                .build();

        UserStatsResponse result = mapper.toStatsResponse(user);

        assertThat(result.getUserId()).isEqualTo("user-001");
        assertThat(result.getTotalXp()).isEqualTo(500);
        assertThat(result.getWeeklyXp()).isEqualTo(200);
        assertThat(result.isRankingOptIn()).isTrue();
        assertThat(result.getTotalBadgesEarned()).isEqualTo(3);
    }

    @Test
    @DisplayName("toStatsResponse con 0 insignias")
    void toStatsResponse_shouldHandleZeroBadges() {
        UserGamification user = UserGamification.builder()
                .userId("user-002")
                .totalXp(0)
                .weeklyXp(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();

        UserStatsResponse result = mapper.toStatsResponse(user);

        assertThat(result.getTotalBadgesEarned()).isEqualTo(0);
    }

    @Test
    @DisplayName("toEarnedBadgeResponse mapea EarnedBadge a EarnedBadgeResponse")
    void toEarnedBadgeResponse_shouldMapCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        EarnedBadge earned = EarnedBadge.builder()
                .badgeId("badge-001")
                .badgeName("Primer Parche")
                .earnedAt(now)
                .xpAwarded(100)
                .build();

        EarnedBadgeResponse result = mapper.toEarnedBadgeResponse(earned);

        assertThat(result.getBadgeId()).isEqualTo("badge-001");
        assertThat(result.getBadgeName()).isEqualTo("Primer Parche");
        assertThat(result.getEarnedAt()).isEqualTo(now);
        assertThat(result.getXpAwarded()).isEqualTo(100);
    }

    @Test
    @DisplayName("toProgressResponse calcula porcentaje correctamente")
    void toProgressResponse_shouldCalculatePercentage() {
        BadgeProgress progress = BadgeProgress.builder()
                .badgeId("badge-001")
                .currentValue(50)
                .requiredValue(100)
                .completed(false)
                .build();

        BadgeProgressResponse result = mapper.toProgressResponse(progress);

        assertThat(result.getBadgeId()).isEqualTo("badge-001");
        assertThat(result.getCurrentValue()).isEqualTo(50);
        assertThat(result.getRequiredValue()).isEqualTo(100);
        assertThat(result.isCompleted()).isFalse();
        assertThat(result.getPercentageComplete()).isEqualTo(50);
    }

    @Test
    @DisplayName("toProgressResponse con requiredValue 0 retorna porcentaje 0")
    void toProgressResponse_shouldReturnZeroPercentage_whenRequiredIsZero() {
        BadgeProgress progress = BadgeProgress.builder()
                .badgeId("badge-002")
                .currentValue(100)
                .requiredValue(0)
                .completed(false)
                .build();

        BadgeProgressResponse result = mapper.toProgressResponse(progress);

        assertThat(result.getPercentageComplete()).isEqualTo(0);
    }

    @Test
    @DisplayName("toProgressResponse limita porcentaje a 100")
    void toProgressResponse_shouldCapPercentageAt100() {
        BadgeProgress progress = BadgeProgress.builder()
                .badgeId("badge-003")
                .currentValue(200)
                .requiredValue(100)
                .completed(true)
                .build();

        BadgeProgressResponse result = mapper.toProgressResponse(progress);

        assertThat(result.getPercentageComplete()).isEqualTo(100);
    }

    @Test
    @DisplayName("toProgressResponse con 0 completado")
    void toProgressResponse_shouldHandleZeroProgress() {
        BadgeProgress progress = BadgeProgress.builder()
                .badgeId("badge-004")
                .currentValue(0)
                .requiredValue(100)
                .completed(false)
                .build();

        BadgeProgressResponse result = mapper.toProgressResponse(progress);

        assertThat(result.getPercentageComplete()).isEqualTo(0);
    }
}
