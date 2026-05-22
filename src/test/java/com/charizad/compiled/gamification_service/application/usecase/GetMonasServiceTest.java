package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.MonaDetailResponse;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.MonaProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedMona;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMonasServiceTest {

    @Mock private MonaRepositoryPort MonaRepository;
    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private GetMonasService service;

    private Mona Mona(String id, String name, MonaCategory cat) {
        return Mona.builder().id(id).name(name).description("desc-" + name)
                .category(cat).active(true).build();
    }

    private UserGamification emptyUser(String userId) {
        return UserGamification.builder()
                .userId(userId).totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Sin usuario gamificación — todas las monas unlocked=false")
    void execute_noUser_allUnlocked() {
        when(MonaRepository.findAllActive()).thenReturn(List.of(
                Mona("b1", "Primera Conexión", MonaCategory.COMMON),
                Mona("b2", "Conector", MonaCategory.UNCOMMON)
        ));
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        List<MonaDetailResponse> result = service.execute("u1");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(m -> !m.isUnlocked());
        assertThat(result).allMatch(m -> m.getEarnedAt() == null);
        assertThat(result).allMatch(m -> m.getProgressPercentage() == 0.0f);
    }

    @Test
    @DisplayName("Usuario con mona ganada — unlocked=true, earnedAt=fecha, progressPercentage=100")
    void execute_earnedMona_unlockedTrue() {
        Mona b1 = Mona("b1", "Asistente", MonaCategory.RARE);
        when(MonaRepository.findAllActive()).thenReturn(List.of(b1));

        LocalDateTime earned = LocalDateTime.of(2026, 5, 19, 10, 0);
        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(50).weeklyXp(0).weeklyMonas(1)
                .rankingOptIn(false)
                .earnedMonas(List.of(EarnedMona.builder()
                        .monaId("b1").monaName("Asistente")
                        .earnedAt(earned).xpAwarded(50).build()))
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));

        List<MonaDetailResponse> result = service.execute("u1");

        assertThat(result).hasSize(1);
        MonaDetailResponse mona = result.get(0);
        assertThat(mona.isUnlocked()).isTrue();
        assertThat(mona.getEarnedAt()).isEqualTo(earned.toLocalDate());
        assertThat(mona.getProgressPercentage()).isEqualTo(100.0f);
        assertThat(mona.getCurrentCount()).isEqualTo(mona.getTargetCount());
    }

    @Test
    @DisplayName("Usuario con progreso parcial — currentCount y progressPercentage correctos")
    void execute_partialProgress_correctPercentage() {
        Mona b1 = Mona("b1", "Conector", MonaCategory.UNCOMMON);
        when(MonaRepository.findAllActive()).thenReturn(List.of(b1));

        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(List.of(MonaProgress.builder()
                        .monaId("b1").currentValue(3).requiredValue(5).completed(false).build()))
                .earnedRewards(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));

        List<MonaDetailResponse> result = service.execute("u1");

        MonaDetailResponse mona = result.get(0);
        assertThat(mona.isUnlocked()).isFalse();
        assertThat(mona.getCurrentCount()).isEqualTo(3);
        assertThat(mona.getTargetCount()).isEqualTo(5);
        assertThat(mona.getProgressPercentage()).isEqualTo(60.0f);
    }

    @Test
    @DisplayName("Catálogo vacío retorna lista vacía")
    void execute_emptyCatalog_returnsEmpty() {
        when(MonaRepository.findAllActive()).thenReturn(List.of());
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        assertThat(service.execute("u1")).isEmpty();
    }

    @Test
    @DisplayName("Mona ganada con progreso — targetCount viene del progreso")
    void execute_earnedWithProgress_targetFromProgress() {
        Mona b1 = Mona("b1", "Conector", MonaCategory.UNCOMMON);
        when(MonaRepository.findAllActive()).thenReturn(List.of(b1));

        LocalDateTime earned = LocalDateTime.now();
        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(25).weeklyXp(0).weeklyMonas(1)
                .rankingOptIn(false)
                .earnedMonas(List.of(EarnedMona.builder()
                        .monaId("b1").monaName("Conector")
                        .earnedAt(earned).xpAwarded(25).build()))
                .progress(List.of(MonaProgress.builder()
                        .monaId("b1").currentValue(5).requiredValue(5).completed(true).build()))
                .earnedRewards(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));

        List<MonaDetailResponse> result = service.execute("u1");

        MonaDetailResponse mona = result.get(0);
        assertThat(mona.isUnlocked()).isTrue();
        assertThat(mona.getTargetCount()).isEqualTo(5);
        assertThat(mona.getCurrentCount()).isEqualTo(5);
        assertThat(mona.getProgressPercentage()).isEqualTo(100.0f);
    }

    @Test
    @DisplayName("buildMonaDetailResponse — targetCount=0 da progressPercentage=0")
    void buildMonaDetailResponse_zeroTarget_zeroPercentage() {
        Mona b1 = Mona("b1", "Test", MonaCategory.COMMON);
        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(List.of(MonaProgress.builder()
                        .monaId("b1").currentValue(0).requiredValue(0).completed(false).build()))
                .earnedRewards(new ArrayList<>())
                .build();

        MonaDetailResponse mona = GetMonasService.buildMonaDetailResponse(b1, user);

        assertThat(mona.getProgressPercentage()).isEqualTo(0.0f);
    }

    @Test
    @DisplayName("buildMonaDetailResponse — progreso > target se limita a 100")
    void buildMonaDetailResponse_overflowProgress_cappedAt100() {
        Mona b1 = Mona("b1", "Test", MonaCategory.COMMON);
        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(List.of(MonaProgress.builder()
                        .monaId("b1").currentValue(10).requiredValue(5).completed(false).build()))
                .earnedRewards(new ArrayList<>())
                .build();

        MonaDetailResponse mona = GetMonasService.buildMonaDetailResponse(b1, user);

        assertThat(mona.getProgressPercentage()).isEqualTo(100.0f);
    }

    @Test
    @DisplayName("buildMonaDetailResponse campos base correctos")
    void buildMonaDetailResponse_baseFields() {
        Mona b1 = Mona("b1", "Primer Parche", MonaCategory.COMMON);

        MonaDetailResponse mona = GetMonasService.buildMonaDetailResponse(b1, null);

        assertThat(mona.getMonaId()).isEqualTo("b1");
        assertThat(mona.getName()).isEqualTo("Primer Parche");
        assertThat(mona.getDescription()).isEqualTo("desc-Primer Parche");
        assertThat(mona.getRarity()).isEqualTo(MonaCategory.COMMON);
    }
}
