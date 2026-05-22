package com.charizad.compiled.gamification_service.infrastructure.scheduler;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeeklyXpResetSchedulerTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private WeeklyXpResetScheduler scheduler;

    @Test
    @DisplayName("resetWeeklyXp reinicia XP de usuarios opt-in y guarda")
    void resetWeeklyXp_shouldResetXpAndSave() {
        UserGamification user1 = UserGamification.builder()
                .userId("user-001")
                .totalXp(500)
                .weeklyXp(200)
                .weeklyMonas(3)
                .rankingOptIn(true)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();

        UserGamification user2 = UserGamification.builder()
                .userId("user-002")
                .totalXp(300)
                .weeklyXp(100)
                .weeklyMonas(1)
                .rankingOptIn(true)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();

        when(userGamificationRepository.findAllOptedIn()).thenReturn(List.of(user1, user2));

        scheduler.resetWeeklyXp();

        assertThat(user1.getWeeklyXp()).isZero();
        assertThat(user2.getWeeklyXp()).isZero();
        assertThat(user1.getWeeklyMonas()).isZero();
        assertThat(user2.getWeeklyMonas()).isZero();
        assertThat(user1.getTotalXp()).isEqualTo(500);
        assertThat(user2.getTotalXp()).isEqualTo(300);

        verify(userGamificationRepository).findAllOptedIn();
        verify(userGamificationRepository).saveAll(List.of(user1, user2));
    }

    @Test
    @DisplayName("resetWeeklyXp no falla cuando no hay usuarios")
    void resetWeeklyXp_shouldHandleEmptyUsers() {
        when(userGamificationRepository.findAllOptedIn()).thenReturn(List.of());

        scheduler.resetWeeklyXp();

        verify(userGamificationRepository).findAllOptedIn();
        verify(userGamificationRepository).saveAll(List.of());
    }

    @Test
    @DisplayName("resetMonthlyMonas reinicia monas mensuales y guarda")
    void resetMonthlyMonas_shouldResetAndSave() {
        UserGamification user = UserGamification.builder()
                .userId("user-001").totalXp(500).monthlyMonas(10).rankingOptIn(true)
                .earnedBadges(new ArrayList<>()).progress(new ArrayList<>())
                .build();

        when(userGamificationRepository.findAllOptedIn()).thenReturn(List.of(user));

        scheduler.resetMonthlyMonas();

        assertThat(user.getMonthlyMonas()).isZero();
        assertThat(user.getTotalXp()).isEqualTo(500);
        verify(userGamificationRepository).saveAll(List.of(user));
    }

    @Test
    @DisplayName("resetSemestralMonas reinicia monas semestrales y guarda")
    void resetSemestralMonas_shouldResetAndSave() {
        UserGamification user = UserGamification.builder()
                .userId("user-001").totalXp(500).semestralMonas(15).rankingOptIn(true)
                .earnedBadges(new ArrayList<>()).progress(new ArrayList<>())
                .build();

        when(userGamificationRepository.findAllOptedIn()).thenReturn(List.of(user));

        scheduler.resetSemestralMonas();

        assertThat(user.getSemestralMonas()).isZero();
        assertThat(user.getTotalXp()).isEqualTo(500);
        verify(userGamificationRepository).saveAll(List.of(user));
    }
}
