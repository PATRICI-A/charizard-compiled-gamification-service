package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMonasServiceTest {

    private static final UUID B1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID B2 = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Mock private BadgeRepositoryPort badgeRepository;
    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private GetMonasService service;

    private Badge badge(UUID id, String name, BadgeCategory cat) {
        return Badge.builder().id(id).name(name).description("desc-" + name)
                .category(cat).active(true).build();
    }

    private UserGamification emptyUser(String userId) {
        return UserGamification.builder()
                .userId(userId).totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Sin usuario gamificación — todas las monas unlocked=false")
    void execute_noUser_allUnlocked() {
        when(badgeRepository.findAllActive()).thenReturn(List.of(
                badge(B1, "Primera Conexión", BadgeCategory.COMMON),
                badge(B2, "Conector", BadgeCategory.UNCOMMON)
        ));
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        List<MonaResponse> result = service.execute("u1");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(m -> !m.isUnlocked());
        assertThat(result).allMatch(m -> m.getEarnedAt() == null);
        assertThat(result).allMatch(m -> m.getProgressPercentage() == 0.0f);
    }

    @Test
    @DisplayName("Usuario con mona ganada — unlocked=true, earnedAt=fecha, progressPercentage=100")
    void execute_earnedBadge_unlockedTrue() {
        Badge b1 = badge(B1, "Asistente", BadgeCategory.RARE);
        when(badgeRepository.findAllActive()).thenReturn(List.of(b1));

        LocalDateTime earned = LocalDateTime.of(2026, 5, 19, 10, 0);
        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(50).weeklyXp(0).weeklyMonas(1)
                .rankingOptIn(false)
                .earnedBadges(List.of(EarnedBadge.builder()
                        .badgeId(B1).badgeName("Asistente")
                        .earnedAt(earned).xpAwarded(50).build()))
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));

        List<MonaResponse> result = service.execute("u1");

        assertThat(result).hasSize(1);
        MonaResponse mona = result.get(0);
        assertThat(mona.isUnlocked()).isTrue();
        assertThat(mona.getEarnedAt()).isEqualTo(earned.toLocalDate());
        assertThat(mona.getProgressPercentage()).isEqualTo(100.0f);
        assertThat(mona.getCurrentCount()).isEqualTo(mona.getTargetCount());
    }

    @Test
    @DisplayName("Usuario con progreso parcial — currentCount y progressPercentage correctos")
    void execute_partialProgress_correctPercentage() {
        Badge b1 = badge(B1, "Conector", BadgeCategory.UNCOMMON);
        when(badgeRepository.findAllActive()).thenReturn(List.of(b1));

        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(List.of(BadgeProgress.builder()
                        .badgeId(B1).currentValue(3).requiredValue(5).completed(false).build()))
                .earnedRewards(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));

        List<MonaResponse> result = service.execute("u1");

        MonaResponse mona = result.get(0);
        assertThat(mona.isUnlocked()).isFalse();
        assertThat(mona.getCurrentCount()).isEqualTo(3);
        assertThat(mona.getTargetCount()).isEqualTo(5);
        assertThat(mona.getProgressPercentage()).isEqualTo(60.0f);
    }

    @Test
    @DisplayName("Catálogo vacío retorna lista vacía")
    void execute_emptyCatalog_returnsEmpty() {
        when(badgeRepository.findAllActive()).thenReturn(List.of());
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        assertThat(service.execute("u1")).isEmpty();
    }

    @Test
    @DisplayName("Mona ganada con progreso — targetCount viene del progreso")
    void execute_earnedWithProgress_targetFromProgress() {
        Badge b1 = badge(B1, "Conector", BadgeCategory.UNCOMMON);
        when(badgeRepository.findAllActive()).thenReturn(List.of(b1));

        LocalDateTime earned = LocalDateTime.now();
        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(25).weeklyXp(0).weeklyMonas(1)
                .rankingOptIn(false)
                .earnedBadges(List.of(EarnedBadge.builder()
                        .badgeId(B1).badgeName("Conector")
                        .earnedAt(earned).xpAwarded(25).build()))
                .progress(List.of(BadgeProgress.builder()
                        .badgeId(B1).currentValue(5).requiredValue(5).completed(true).build()))
                .earnedRewards(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));

        List<MonaResponse> result = service.execute("u1");

        MonaResponse mona = result.get(0);
        assertThat(mona.isUnlocked()).isTrue();
        assertThat(mona.getTargetCount()).isEqualTo(5);
        assertThat(mona.getCurrentCount()).isEqualTo(5);
        assertThat(mona.getProgressPercentage()).isEqualTo(100.0f);
    }

    @Test
    @DisplayName("buildMonaResponse — targetCount=0 da progressPercentage=0")
    void buildMonaResponse_zeroTarget_zeroPercentage() {
        Badge b1 = badge(B1, "Test", BadgeCategory.COMMON);
        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(List.of(BadgeProgress.builder()
                        .badgeId(B1).currentValue(0).requiredValue(0).completed(false).build()))
                .earnedRewards(new ArrayList<>())
                .build();

        MonaResponse mona = GetMonasService.buildMonaResponse(b1, user);

        assertThat(mona.getProgressPercentage()).isEqualTo(0.0f);
    }

    @Test
    @DisplayName("buildMonaResponse — progreso > target se limita a 100")
    void buildMonaResponse_overflowProgress_cappedAt100() {
        Badge b1 = badge(B1, "Test", BadgeCategory.COMMON);
        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(List.of(BadgeProgress.builder()
                        .badgeId(B1).currentValue(10).requiredValue(5).completed(false).build()))
                .earnedRewards(new ArrayList<>())
                .build();

        MonaResponse mona = GetMonasService.buildMonaResponse(b1, user);

        assertThat(mona.getProgressPercentage()).isEqualTo(100.0f);
    }

    @Test
    @DisplayName("buildMonaResponse campos base correctos")
    void buildMonaResponse_baseFields() {
        Badge b1 = badge(B1, "Primer Parche", BadgeCategory.COMMON);

        MonaResponse mona = GetMonasService.buildMonaResponse(b1, null);

        assertThat(mona.getMonaId()).isEqualTo(B1);
        assertThat(mona.getName()).isEqualTo("Primer Parche");
        assertThat(mona.getDescription()).isEqualTo("desc-Primer Parche");
        assertThat(mona.getRarity()).isEqualTo(BadgeCategory.COMMON);
    }
}
