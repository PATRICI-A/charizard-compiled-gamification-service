package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRankingPositionServiceTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private GetRankingPositionService service;

    private UserGamification buildUser(String userId, boolean optIn, int weeklyMonas, int badgeCount) {
        ArrayList<com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge> badges = new ArrayList<>();
        for (int i = 0; i < badgeCount; i++) {
            badges.add(com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge.builder()
                    .badgeId("b" + i).badgeName("Badge" + i).xpAwarded(10).build());
        }
        return UserGamification.builder()
                .id("ug-1").userId(userId)
                .totalXp(badgeCount * 10).weeklyXp(0)
                .weeklyMonas(weeklyMonas)
                .rankingOptIn(optIn)
                .earnedBadges(badges)
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Usuario no encontrado retorna posicion 0 con nivel por defecto")
    void execute_userNotFound_returnsPosition0() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());
        when(userGamificationRepository.countAllOptedIn()).thenReturn(50L);

        RankingPositionResponse response = service.execute("u1");

        assertThat(response.getPosicion()).isEqualTo(0);
        assertThat(response.getTotalParticipantes()).isEqualTo(50L);
        assertThat(response.getMonasThisWeek()).isEqualTo(0);
        assertThat(response.getUserId()).isEqualTo("u1");
    }

    @Test
    @DisplayName("Usuario sin opt-in retorna posicion 0")
    void execute_userNotOptedIn_returnsPosition0() {
        UserGamification user = buildUser("u1", false, 5, 3);
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));
        when(userGamificationRepository.countAllOptedIn()).thenReturn(20L);

        RankingPositionResponse response = service.execute("u1");

        assertThat(response.getPosicion()).isEqualTo(0);
        assertThat(response.getTotalParticipantes()).isEqualTo(20L);
        verify(userGamificationRepository, never()).countOptedInWithMoreMonasThan(anyInt());
    }

    @Test
    @DisplayName("Usuario con opt-in calcula posición correctamente")
    void execute_userOptedIn_calculatesPosition() {
        UserGamification user = buildUser("u1", true, 8, 5);
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));
        when(userGamificationRepository.countAllOptedIn()).thenReturn(100L);
        when(userGamificationRepository.countOptedInWithMoreMonasThan(8)).thenReturn(3L);

        RankingPositionResponse response = service.execute("u1");

        assertThat(response.getPosicion()).isEqualTo(4); // 3 ahead + 1
        assertThat(response.getTotalParticipantes()).isEqualTo(100L);
        assertThat(response.getMonasThisWeek()).isEqualTo(8);
        assertThat(response.getUserId()).isEqualTo("u1");
    }

    @Test
    @DisplayName("Usuario en primera posición cuando nadie tiene más monas")
    void execute_userFirst_position1() {
        UserGamification user = buildUser("u1", true, 15, 6);
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));
        when(userGamificationRepository.countAllOptedIn()).thenReturn(10L);
        when(userGamificationRepository.countOptedInWithMoreMonasThan(15)).thenReturn(0L);

        RankingPositionResponse response = service.execute("u1");

        assertThat(response.getPosicion()).isEqualTo(1);
    }
}
