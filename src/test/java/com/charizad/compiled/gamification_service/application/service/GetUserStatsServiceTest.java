package com.charizad.compiled.gamification_service.application.service;

import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserStatsServiceTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;
    @Mock private UserGamificationMapper userGamificationMapper;

    @InjectMocks
    private GetUserStatsService service;

    @Test
    @DisplayName("Retorna estadísticas del usuario correctamente")
    void execute_shouldReturnStats_whenUserExists() {
        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(350)
                .weeklyXp(150)
                .rankingOptIn(true)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();

        UserStatsResponse expected = UserStatsResponse.builder()
                .userId("user-001")
                .totalXp(350)
                .weeklyXp(150)
                .rankingOptIn(true)
                .totalBadgesEarned(0)
                .build();

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));
        when(userGamificationMapper.toStatsResponse(user)).thenReturn(expected);

        UserStatsResponse result = service.execute("user-001");

        assertThat(result.getTotalXp()).isEqualTo(350);
        assertThat(result.getWeeklyXp()).isEqualTo(150);
        assertThat(result.isRankingOptIn()).isTrue();
    }

    @Test
    @DisplayName("Lanza excepción si el usuario no existe")
    void execute_shouldThrow_whenUserNotFound() {
        when(userGamificationRepository.findByUserId("user-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("user-999"))
                .isInstanceOf(UserGamificationNotFoundException.class);
    }
}
