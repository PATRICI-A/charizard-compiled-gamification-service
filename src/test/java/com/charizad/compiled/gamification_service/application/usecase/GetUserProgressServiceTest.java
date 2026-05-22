package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.MonaProgressResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.MonaProgress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserProgressServiceTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;
    @Mock private UserGamificationMapper userGamificationMapper;

    @InjectMocks
    private GetUserProgressService service;

    @Test
    @DisplayName("Retorna progreso de insignias del usuario")
    void execute_shouldReturnProgress_whenUserExists() {
        MonaProgress progress = MonaProgress.builder()
                .monaId("Mona-001")
                .currentValue(50)
                .requiredValue(100)
                .completed(false)
                .build();

        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(100)
                .weeklyXp(50)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(new ArrayList<>(List.of(progress)))
                .build();

        MonaProgressResponse expectedResponse = MonaProgressResponse.builder()
                .monaId("Mona-001")
                .currentValue(50)
                .requiredValue(100)
                .completed(false)
                .percentageComplete(50)
                .build();

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));
        when(userGamificationMapper.toProgressResponse(progress)).thenReturn(expectedResponse);

        List<MonaProgressResponse> result = service.execute("user-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMonaId()).isEqualTo("Mona-001");
        assertThat(result.get(0).getPercentageComplete()).isEqualTo(50);
    }

    @Test
    @DisplayName("Retorna lista vacía si el usuario no tiene progreso")
    void execute_shouldReturnEmptyList_whenNoProgress() {
        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(0)
                .weeklyXp(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));

        List<MonaProgressResponse> result = service.execute("user-001");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Lanza excepción si el usuario no existe")
    void execute_shouldThrow_whenUserNotFound() {
        when(userGamificationRepository.findByUserId("user-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("user-999"))
                .isInstanceOf(UserGamificationNotFoundException.class)
                .hasMessageContaining("user-999");
    }
}
