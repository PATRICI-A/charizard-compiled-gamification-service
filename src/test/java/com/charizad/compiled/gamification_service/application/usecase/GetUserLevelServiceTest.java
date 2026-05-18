package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.UserLevelResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
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

    private UserGamification buildUser(String userId, int badgeCount) {
        ArrayList<EarnedBadge> badges = new ArrayList<>();
        for (int i = 0; i < badgeCount; i++) {
            badges.add(EarnedBadge.builder().badgeId("b" + i).badgeName("B" + i).xpAwarded(10).build());
        }
        return UserGamification.builder()
                .id("ug-1").userId(userId)
                .totalXp(badgeCount * 10).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedBadges(badges)
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
    @DisplayName("Usuario con 0 monas está en nivel 1")
    void execute_0monas_nivel1() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(buildUser("u1", 0)));

        UserLevelResponse response = service.execute("u1");

        assertThat(response.getNivel()).isEqualTo(1);
        assertThat(response.getTotalMonas()).isEqualTo(0);
        assertThat(response.getUserId()).isEqualTo("u1");
        assertThat(response.getMonasParaSiguienteNivel()).isEqualTo(3); // threshold[1] - 0 = 3
    }

    @Test
    @DisplayName("Usuario con 3 monas está en nivel 2")
    void execute_3monas_nivel2() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(buildUser("u1", 3)));

        UserLevelResponse response = service.execute("u1");

        assertThat(response.getNivel()).isEqualTo(2);
        assertThat(response.getTotalMonas()).isEqualTo(3);
        assertThat(response.getMonasParaSiguienteNivel()).isEqualTo(3); // threshold[2]=6 - 3 = 3
    }

    @Test
    @DisplayName("Usuario con 6 monas está en nivel 3")
    void execute_6monas_nivel3() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(buildUser("u1", 6)));

        UserLevelResponse response = service.execute("u1");

        assertThat(response.getNivel()).isEqualTo(3);
        assertThat(response.getTotalMonas()).isEqualTo(6);
    }

    @Test
    @DisplayName("Usuario con 13 monas está en nivel máximo 5")
    void execute_13monas_nivel5() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(buildUser("u1", 13)));

        UserLevelResponse response = service.execute("u1");

        assertThat(response.getNivel()).isEqualTo(5);
        assertThat(response.getTotalMonas()).isEqualTo(13);
        assertThat(response.getMonasParaSiguienteNivel()).isEqualTo(0);
    }
}
