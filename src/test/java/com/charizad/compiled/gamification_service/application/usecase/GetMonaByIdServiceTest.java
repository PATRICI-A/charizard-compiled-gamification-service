package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.MonaDetailResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.MonaNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMonaByIdServiceTest {

    @Mock private MonaRepositoryPort MonaRepository;
    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private GetMonaByIdService service;

    private Mona Mona(String id, String name) {
        return Mona.builder().id(id).name(name).description("desc")
                .category(MonaCategory.COMMON).active(true).build();
    }

    @Test
    @DisplayName("Mona no encontrado → MonaNotFoundException")
    void execute_MonaNotFound_throws() {
        when(MonaRepository.findById("b-missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("u1", "b-missing"))
                .isInstanceOf(MonaNotFoundException.class);
    }

    @Test
    @DisplayName("Mona encontrado, sin usuario → unlocked=false")
    void execute_noUser_unlockedFalse() {
        when(MonaRepository.findById("b1")).thenReturn(Optional.of(Mona("b1", "Test")));
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        MonaDetailResponse result = service.execute("u1", "b1");

        assertThat(result.getMonaId()).isEqualTo("b1");
        assertThat(result.isUnlocked()).isFalse();
    }

    @Test
    @DisplayName("Mona encontrado, usuario con mona ganada → unlocked=true")
    void execute_userEarnedMona_unlockedTrue() {
        Mona b = Mona("b1", "Primer Parche");
        when(MonaRepository.findById("b1")).thenReturn(Optional.of(b));

        LocalDateTime earned = LocalDateTime.of(2026, 5, 1, 12, 0);
        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(25).weeklyXp(0).weeklyMonas(1)
                .rankingOptIn(false)
                .earnedMonas(List.of(EarnedMona.builder()
                        .monaId("b1").monaName("Primer Parche")
                        .earnedAt(earned).xpAwarded(25).build()))
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));

        MonaDetailResponse result = service.execute("u1", "b1");

        assertThat(result.isUnlocked()).isTrue();
        assertThat(result.getEarnedAt()).isEqualTo(earned.toLocalDate());
        assertThat(result.getProgressPercentage()).isEqualTo(100.0f);
    }
}
