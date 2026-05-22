package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserMonasServiceTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;
    @Mock private UserGamificationMapper userGamificationMapper;

    @InjectMocks
    private GetUserMonasService service;

    @Test
    @DisplayName("Retorna lista de insignias del usuario")
    void execute_shouldReturnMonas_whenUserExists() {
        EarnedMona earned = EarnedMona.builder()
                .monaId("Mona-001")
                .monaName("Primer Parche")
                .earnedAt(LocalDateTime.now())
                .xpAwarded(100)
                .build();

        ArrayList<EarnedMona> earnedList = new ArrayList<>();
        earnedList.add(earned);

        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(100)
                .weeklyXp(100)
                .rankingOptIn(false)
                .earnedMonas(earnedList)
                .progress(new ArrayList<>())
                .build();

        EarnedMonaResponse expectedResponse = EarnedMonaResponse.builder()
                .monaId("Mona-001")
                .monaName("Primer Parche")
                .xpAwarded(100)
                .build();

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));
        when(userGamificationMapper.toEarnedMonaResponse(earned)).thenReturn(expectedResponse);

        List<EarnedMonaResponse> result = service.execute("user-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMonaId()).isEqualTo("Mona-001");
    }

    @Test
    @DisplayName("Retorna lista vacía si el usuario no tiene insignias")
    void execute_shouldReturnEmptyList_whenUserHasNoMonas() {
        UserGamification user = UserGamification.newUser("user-001");

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));

        List<EarnedMonaResponse> result = service.execute("user-001");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Lanza excepción si el usuario no tiene perfil de gamificación")
    void execute_shouldThrow_whenUserNotFound() {
        when(userGamificationRepository.findByUserId("user-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("user-999"))
                .isInstanceOf(UserGamificationNotFoundException.class)
                .hasMessageContaining("user-999");
    }
}
