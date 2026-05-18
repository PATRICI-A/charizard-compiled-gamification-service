package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.infrastructure.adapters.out.feign.UserProfileClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRankingServiceTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;
    @Mock private UserProfileClient userProfileClient;

    @InjectMocks
    private GetRankingService service;

    @Test
    @DisplayName("Retorna ranking ordenado con posiciones correctas")
    void execute_shouldReturnRankingWithCorrectPositions() {
        List<UserGamification> users = List.of(
                buildUser("user-A", 5),
                buildUser("user-B", 3),
                buildUser("user-C", 1)
        );

        when(userGamificationRepository.findAllOptedInOrderByWeeklyMonasDesc(10)).thenReturn(users);
        when(userProfileClient.getDisplayName(anyString())).thenReturn(Optional.empty());

        List<RankingEntryResponse> ranking = service.execute(10);

        assertThat(ranking).hasSize(3);
        assertThat(ranking.get(0).getPosition()).isEqualTo(1);
        assertThat(ranking.get(0).getUserId()).isEqualTo("user-A");
        assertThat(ranking.get(0).getMonasThisWeek()).isEqualTo(5);
        assertThat(ranking.get(1).getPosition()).isEqualTo(2);
        assertThat(ranking.get(2).getPosition()).isEqualTo(3);
    }

    @Test
    @DisplayName("Retorna lista vacía si no hay usuarios opt-in")
    void execute_shouldReturnEmpty_whenNoOptInUsers() {
        when(userGamificationRepository.findAllOptedInOrderByWeeklyMonasDesc(10)).thenReturn(List.of());

        List<RankingEntryResponse> ranking = service.execute(10);

        assertThat(ranking).isEmpty();
    }

    @Test
    @DisplayName("Respeta el límite solicitado")
    void execute_shouldRespectLimit() {
        when(userGamificationRepository.findAllOptedInOrderByWeeklyMonasDesc(3))
                .thenReturn(List.of(
                        buildUser("user-A", 500),
                        buildUser("user-B", 300),
                        buildUser("user-C", 100)
                ));
        when(userProfileClient.getDisplayName(anyString())).thenReturn(Optional.empty());

        List<RankingEntryResponse> ranking = service.execute(3);

        verify(userGamificationRepository).findAllOptedInOrderByWeeklyMonasDesc(3);
        assertThat(ranking).hasSize(3);
    }

    private UserGamification buildUser(String userId, int weeklyMonas) {
        return UserGamification.builder()
                .userId(userId)
                .totalXp(0)
                .weeklyXp(0)
                .weeklyMonas(weeklyMonas)
                .rankingOptIn(true)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();
    }
}
