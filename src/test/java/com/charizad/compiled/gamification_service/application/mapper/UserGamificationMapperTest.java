package com.charizad.compiled.gamification_service.application.mapper;

import com.charizad.compiled.gamification_service.application.dto.response.MonaProgressResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.valueobjects.MonaProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedMona;
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
        List<EarnedMona> Monas = List.of(
                EarnedMona.builder().monaId("b1").build(),
                EarnedMona.builder().monaId("b2").build(),
                EarnedMona.builder().monaId("b3").build()
        );

        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(500)
                .weeklyXp(200)
                .rankingOptIn(true)
                .earnedMonas(Monas)
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();

        UserStatsResponse result = mapper.toStatsResponse(user);

        assertThat(result.getUserId()).isEqualTo("user-001");
        assertThat(result.getTotalXp()).isEqualTo(500);
        assertThat(result.getWeeklyXp()).isEqualTo(200);
        assertThat(result.isRankingOptIn()).isTrue();
        assertThat(result.getTotalMonasEarned()).isEqualTo(3);
    }

    @Test
    @DisplayName("toStatsResponse con 0 insignias")
    void toStatsResponse_shouldHandleZeroMonas() {
        UserGamification user = UserGamification.builder()
                .userId("user-002")
                .totalXp(0)
                .weeklyXp(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();

        UserStatsResponse result = mapper.toStatsResponse(user);

        assertThat(result.getTotalMonasEarned()).isEqualTo(0);
    }

    @Test
    @DisplayName("toEarnedMonaResponse mapea EarnedMona a EarnedMonaResponse")
    void toEarnedMonaResponse_shouldMapCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        EarnedMona earned = EarnedMona.builder()
                .monaId("Mona-001")
                .monaName("Primer Parche")
                .earnedAt(now)
                .xpAwarded(100)
                .build();

        EarnedMonaResponse result = mapper.toEarnedMonaResponse(earned);

        assertThat(result.getMonaId()).isEqualTo("Mona-001");
        assertThat(result.getMonaName()).isEqualTo("Primer Parche");
        assertThat(result.getEarnedAt()).isEqualTo(now);
        assertThat(result.getXpAwarded()).isEqualTo(100);
    }

    @Test
    @DisplayName("toProgressResponse calcula porcentaje correctamente")
    void toProgressResponse_shouldCalculatePercentage() {
        MonaProgress progress = MonaProgress.builder()
                .monaId("Mona-001")
                .currentValue(50)
                .requiredValue(100)
                .completed(false)
                .build();

        MonaProgressResponse result = mapper.toProgressResponse(progress);

        assertThat(result.getMonaId()).isEqualTo("Mona-001");
        assertThat(result.getCurrentValue()).isEqualTo(50);
        assertThat(result.getRequiredValue()).isEqualTo(100);
        assertThat(result.isCompleted()).isFalse();
        assertThat(result.getPercentageComplete()).isEqualTo(50);
    }

    @Test
    @DisplayName("toProgressResponse con requiredValue 0 retorna porcentaje 0")
    void toProgressResponse_shouldReturnZeroPercentage_whenRequiredIsZero() {
        MonaProgress progress = MonaProgress.builder()
                .monaId("Mona-002")
                .currentValue(100)
                .requiredValue(0)
                .completed(false)
                .build();

        MonaProgressResponse result = mapper.toProgressResponse(progress);

        assertThat(result.getPercentageComplete()).isEqualTo(0);
    }

    @Test
    @DisplayName("toProgressResponse limita porcentaje a 100")
    void toProgressResponse_shouldCapPercentageAt100() {
        MonaProgress progress = MonaProgress.builder()
                .monaId("Mona-003")
                .currentValue(200)
                .requiredValue(100)
                .completed(true)
                .build();

        MonaProgressResponse result = mapper.toProgressResponse(progress);

        assertThat(result.getPercentageComplete()).isEqualTo(100);
    }

    @Test
    @DisplayName("toProgressResponse con 0 completado")
    void toProgressResponse_shouldHandleZeroProgress() {
        MonaProgress progress = MonaProgress.builder()
                .monaId("Mona-004")
                .currentValue(0)
                .requiredValue(100)
                .completed(false)
                .build();

        MonaProgressResponse result = mapper.toProgressResponse(progress);

        assertThat(result.getPercentageComplete()).isEqualTo(0);
    }
}
