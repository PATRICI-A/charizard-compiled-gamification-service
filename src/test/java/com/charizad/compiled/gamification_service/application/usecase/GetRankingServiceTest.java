package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
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
    @DisplayName("Returns weekly ranking with correct positions")
    void execute_weekly_shouldReturnRankingWithCorrectPositions() {
        List<UserGamification> users = List.of(
                buildUser("user-A", 5, 0, 0),
                buildUser("user-B", 3, 0, 0),
                buildUser("user-C", 1, 0, 0)
        );

        when(userGamificationRepository.findAllOptedInRankedFor(RankingType.WEEKLY)).thenReturn(users);
        when(userProfileClient.getDisplayName(anyString())).thenReturn(Optional.empty());

        List<RankingEntryResponse> ranking = service.execute(RankingType.WEEKLY);

        assertThat(ranking).hasSize(3);
        assertThat(ranking.get(0).getPosition()).isEqualTo(1);
        assertThat(ranking.get(0).getUserId()).isEqualTo("user-A");
        assertThat(ranking.get(0).getMonasThisPeriod()).isEqualTo(5);
        assertThat(ranking.get(0).getType()).isEqualTo("WEEKLY");
        assertThat(ranking.get(1).getPosition()).isEqualTo(2);
        assertThat(ranking.get(2).getPosition()).isEqualTo(3);
    }

    @Test
    @DisplayName("Returns empty list when no opted-in users")
    void execute_shouldReturnEmpty_whenNoOptInUsers() {
        when(userGamificationRepository.findAllOptedInRankedFor(RankingType.WEEKLY)).thenReturn(List.of());

        List<RankingEntryResponse> ranking = service.execute(RankingType.WEEKLY);

        assertThat(ranking).isEmpty();
    }

    @Test
    @DisplayName("Returns monthly ranking using monthlyMonas")
    void execute_monthly_shouldUseMonthlyMonas() {
        List<UserGamification> users = List.of(
                buildUser("user-A", 1, 10, 0),
                buildUser("user-B", 1, 7, 0)
        );

        when(userGamificationRepository.findAllOptedInRankedFor(RankingType.MONTHLY)).thenReturn(users);
        when(userProfileClient.getDisplayName(anyString())).thenReturn(Optional.empty());

        List<RankingEntryResponse> ranking = service.execute(RankingType.MONTHLY);

        assertThat(ranking.get(0).getMonasThisPeriod()).isEqualTo(10);
        assertThat(ranking.get(0).getType()).isEqualTo("MONTHLY");
    }

    @Test
    @DisplayName("Returns semester ranking using semestralMonas")
    void execute_semester_shouldUseSemestralMonas() {
        List<UserGamification> users = List.of(
                buildUser("user-A", 1, 0, 25)
        );

        when(userGamificationRepository.findAllOptedInRankedFor(RankingType.SEMESTER)).thenReturn(users);
        when(userProfileClient.getDisplayName(anyString())).thenReturn(Optional.empty());

        List<RankingEntryResponse> ranking = service.execute(RankingType.SEMESTER);

        assertThat(ranking.get(0).getMonasThisPeriod()).isEqualTo(25);
        assertThat(ranking.get(0).getType()).isEqualTo("SEMESTER");
    }

    @Test
    @DisplayName("Uses userId as displayName when profile client returns empty")
    void execute_shouldFallbackToUserId_whenDisplayNameAbsent() {
        when(userGamificationRepository.findAllOptedInRankedFor(RankingType.WEEKLY))
                .thenReturn(List.of(buildUser("user-Z", 2, 0, 0)));
        when(userProfileClient.getDisplayName("user-Z")).thenReturn(Optional.empty());

        List<RankingEntryResponse> ranking = service.execute(RankingType.WEEKLY);

        assertThat(ranking.get(0).getDisplayName()).isEqualTo("user-Z");
    }

    private UserGamification buildUser(String userId, int weeklyMonas, int monthlyMonas, int semestralMonas) {
        return UserGamification.builder()
                .userId(userId)
                .totalXp(0)
                .weeklyXp(0)
                .weeklyMonas(weeklyMonas)
                .monthlyMonas(monthlyMonas)
                .semestralMonas(semestralMonas)
                .rankingOptIn(true)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();
    }
}
