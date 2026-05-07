package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
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
class GetRankingServiceTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private GetRankingService service;

    @Test
    @DisplayName("Retorna ranking ordenado con posiciones correctas")
    void execute_shouldReturnRankingWithCorrectPositions() {
        List<UserGamification> users = List.of(
                buildUser("user-A", 500),
                buildUser("user-B", 300),
                buildUser("user-C", 100)
        );

        when(userGamificationRepository.findAllOptedInOrderByWeeklyXpDesc(10)).thenReturn(users);

        List<RankingEntryResponse> ranking = service.execute(10);

        assertThat(ranking).hasSize(3);
        assertThat(ranking.get(0).getPosition()).isEqualTo(1);
        assertThat(ranking.get(0).getUserId()).isEqualTo("user-A");
        assertThat(ranking.get(0).getWeeklyXp()).isEqualTo(500);
        assertThat(ranking.get(1).getPosition()).isEqualTo(2);
        assertThat(ranking.get(2).getPosition()).isEqualTo(3);
    }

    @Test
    @DisplayName("Retorna lista vacía si no hay usuarios opt-in")
    void execute_shouldReturnEmpty_whenNoOptInUsers() {
        when(userGamificationRepository.findAllOptedInOrderByWeeklyXpDesc(10)).thenReturn(List.of());

        List<RankingEntryResponse> ranking = service.execute(10);

        assertThat(ranking).isEmpty();
    }

    @Test
    @DisplayName("Respeta el límite solicitado")
    void execute_shouldRespectLimit() {
        when(userGamificationRepository.findAllOptedInOrderByWeeklyXpDesc(3))
                .thenReturn(List.of(
                        buildUser("user-A", 500),
                        buildUser("user-B", 300),
                        buildUser("user-C", 100)
                ));

        List<RankingEntryResponse> ranking = service.execute(3);

        verify(userGamificationRepository).findAllOptedInOrderByWeeklyXpDesc(3);
        assertThat(ranking).hasSize(3);
    }

    private UserGamification buildUser(String userId, int weeklyXp) {
        return UserGamification.builder()
                .userId(userId)
                .totalXp(weeklyXp)
                .weeklyXp(weeklyXp)
                .rankingOptIn(true)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();
    }
}
