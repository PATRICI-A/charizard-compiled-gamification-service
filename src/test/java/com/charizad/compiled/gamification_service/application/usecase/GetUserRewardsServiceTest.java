package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedRewardResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedReward;
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
class GetUserRewardsServiceTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private GetUserRewardsService service;

    @Test
    @DisplayName("Retorna lista de rewards del usuario")
    void execute_shouldReturnRewards_whenUserExists() {
        LocalDateTime now = LocalDateTime.now();
        EarnedReward reward = EarnedReward.builder()
                .rewardId("reward-001")
                .rewardName("Gold Title")
                .rewardType(RewardType.TITLE)
                .unlockedAt(now)
                .xpAtUnlock(1000)
                .build();

        List<EarnedReward> rewards = new ArrayList<>();
        rewards.add(reward);

        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(1000)
                .weeklyXp(200)
                .rankingOptIn(true)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(rewards)
                .build();

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));

        List<EarnedRewardResponse> result = service.execute("user-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRewardId()).isEqualTo("reward-001");
        assertThat(result.get(0).getRewardName()).isEqualTo("Gold Title");
        assertThat(result.get(0).getRewardType()).isEqualTo(RewardType.TITLE);
        assertThat(result.get(0).getXpAtUnlock()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Retorna lista vacía si el usuario no tiene rewards")
    void execute_shouldReturnEmptyList_whenUserHasNoRewards() {
        UserGamification user = UserGamification.newUser("user-001");

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));

        List<EarnedRewardResponse> result = service.execute("user-001");

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
