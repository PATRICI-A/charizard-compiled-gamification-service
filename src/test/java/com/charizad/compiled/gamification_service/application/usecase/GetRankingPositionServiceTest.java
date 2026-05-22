package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRankingPositionServiceTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private GetRankingPositionService service;

    @Test
    @DisplayName("User not found returns null position and rankingOptIn=false")
    void execute_userNotFound_returnsNullPosition() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        RankingPositionResponse response = service.execute("u1", RankingType.WEEKLY);

        assertThat(response.getPosition()).isNull();
        assertThat(response.isRankingOptIn()).isFalse();
        assertThat(response.getRankingType()).isEqualTo(RankingType.WEEKLY);
        assertThat(response.getPeriodStart()).isNotNull();
        assertThat(response.getPeriodEnd()).isNotNull();
        verify(userGamificationRepository, never()).findAllOptedInRankedFor(any());
    }

    @Test
    @DisplayName("User not opted-in returns null position and rankingOptIn=false")
    void execute_userNotOptedIn_returnsNullPosition() {
        UserGamification user = buildUser("u1", false, 5, 3);
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));

        RankingPositionResponse response = service.execute("u1", RankingType.WEEKLY);

        assertThat(response.getPosition()).isNull();
        assertThat(response.isRankingOptIn()).isFalse();
        verify(userGamificationRepository, never()).findAllOptedInRankedFor(any());
    }

    @Test
    @DisplayName("Opted-in user calculates correct position from ranked list")
    void execute_userOptedIn_calculatesPosition() {
        UserGamification u1 = buildUser("u1", true, 8, 5);
        UserGamification u2 = buildUser("u2", true, 12, 3);
        UserGamification u3 = buildUser("u3", true, 4, 2);

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(u1));
        when(userGamificationRepository.findAllOptedInRankedFor(RankingType.WEEKLY))
                .thenReturn(List.of(u2, u1, u3));

        RankingPositionResponse response = service.execute("u1", RankingType.WEEKLY);

        assertThat(response.getPosition()).isEqualTo(2);
        assertThat(response.getMonasThisPeriod()).isEqualTo(8);
        assertThat(response.isRankingOptIn()).isTrue();
        assertThat(response.getRankingType()).isEqualTo(RankingType.WEEKLY);
    }

    @Test
    @DisplayName("Opted-in user in first position returns position 1")
    void execute_userFirst_returnsPosition1() {
        UserGamification u1 = buildUser("u1", true, 15, 6);
        UserGamification u2 = buildUser("u2", true, 10, 4);

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(u1));
        when(userGamificationRepository.findAllOptedInRankedFor(RankingType.WEEKLY))
                .thenReturn(List.of(u1, u2));

        RankingPositionResponse response = service.execute("u1", RankingType.WEEKLY);

        assertThat(response.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("Monthly ranking uses monthlyMonas for the period")
    void execute_monthlyType_usesMonthlyMonas() {
        UUID ugId = UUID.fromString("10000000-0000-0000-0000-000000000001");
        UserGamification u1 = UserGamification.builder()
                .id(ugId).userId("u1")
                .weeklyMonas(3).monthlyMonas(20).semestralMonas(5)
                .rankingOptIn(true)
                .earnedBadges(new ArrayList<>()).progress(new ArrayList<>()).earnedRewards(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(u1));
        when(userGamificationRepository.findAllOptedInRankedFor(RankingType.MONTHLY))
                .thenReturn(List.of(u1));

        RankingPositionResponse response = service.execute("u1", RankingType.MONTHLY);

        assertThat(response.getMonasThisPeriod()).isEqualTo(20);
        assertThat(response.getPosition()).isEqualTo(1);
        assertThat(response.getRankingType()).isEqualTo(RankingType.MONTHLY);
    }

    @Test
    @DisplayName("Semester ranking uses semestralMonas for the period")
    void execute_semesterType_usesSemestralMonas() {
        UUID ugId = UUID.fromString("10000000-0000-0000-0000-000000000001");
        UserGamification u1 = UserGamification.builder()
                .id(ugId).userId("u1")
                .weeklyMonas(3).monthlyMonas(20).semestralMonas(15)
                .rankingOptIn(true)
                .earnedBadges(new ArrayList<>()).progress(new ArrayList<>()).earnedRewards(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(u1));
        when(userGamificationRepository.findAllOptedInRankedFor(RankingType.SEMESTER))
                .thenReturn(List.of(u1));

        RankingPositionResponse response = service.execute("u1", RankingType.SEMESTER);

        assertThat(response.getMonasThisPeriod()).isEqualTo(15);
        assertThat(response.getPosition()).isEqualTo(1);
        assertThat(response.getRankingType()).isEqualTo(RankingType.SEMESTER);
    }

    private UserGamification buildUser(String userId, boolean optIn, int weeklyMonas, int badgeCount) {
        UUID ugId = UUID.fromString("10000000-0000-0000-0000-000000000001");
        ArrayList<EarnedBadge> badges = new ArrayList<>();
        for (int i = 0; i < badgeCount; i++) {
            badges.add(EarnedBadge.builder()
                    .badgeId(UUID.randomUUID()).badgeName("Badge" + i).xpAwarded(10).build());
        }
        return UserGamification.builder()
                .id(ugId).userId(userId)
                .totalXp(badgeCount * 10).weeklyXp(0)
                .weeklyMonas(weeklyMonas)
                .monthlyMonas(0).semestralMonas(0)
                .rankingOptIn(optIn)
                .earnedBadges(badges)
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();
    }
}
