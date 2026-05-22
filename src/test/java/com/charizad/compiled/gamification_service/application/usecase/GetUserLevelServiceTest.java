package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.UserLevelResponse;
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

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserLevelServiceTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private GetUserLevelService service;

    private UserGamification buildUser(String userId, int totalXp) {
        return UserGamification.builder()
                .id("ug-1").userId(userId)
                .totalXp(totalXp).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Lanza UserGamificationNotFoundException cuando usuario no existe")
    void execute_userNotFound_throwsException() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("u1"))
                .isInstanceOf(UserGamificationNotFoundException.class);
    }

    @Test
    @DisplayName("Usuario con 0 XP está en nivel 1")
    void execute_0xp_nivel1() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(buildUser("u1", 0)));

        UserLevelResponse response = service.execute("u1");

        assertThat(response.getNivel()).isEqualTo(1);
        assertThat(response.getTotalXp()).isEqualTo(0);
        assertThat(response.getUserId()).isEqualTo("u1");
        assertThat(response.getXpParaSiguienteNivel()).isEqualTo(500);
    }

    @Test
    @DisplayName("Usuario con 500 XP está en nivel 2")
    void execute_500xp_nivel2() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(buildUser("u1", 500)));

        UserLevelResponse response = service.execute("u1");

        assertThat(response.getNivel()).isEqualTo(2);
        assertThat(response.getTotalXp()).isEqualTo(500);
        assertThat(response.getXpParaSiguienteNivel()).isEqualTo(1500);
    }

    @Test
    @DisplayName("Usuario con 1500 XP está en nivel 3")
    void execute_1500xp_nivel3() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(buildUser("u1", 1500)));

        UserLevelResponse response = service.execute("u1");

        assertThat(response.getNivel()).isEqualTo(3);
        assertThat(response.getTotalXp()).isEqualTo(1500);
    }

    @Test
    @DisplayName("Usuario con 5000 XP está en nivel máximo 5")
    void execute_5000xp_nivel5() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(buildUser("u1", 5000)));

        UserLevelResponse response = service.execute("u1");

        assertThat(response.getNivel()).isEqualTo(5);
        assertThat(response.getTotalXp()).isEqualTo(5000);
        assertThat(response.getXpParaSiguienteNivel()).isEqualTo(0);
    }
}
