package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.domain.model.Reward;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import com.charizad.compiled.gamification_service.domain.ports.out.RewardRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckXpRewardsServiceTest {

    @Mock private RewardRepositoryPort rewardRepository;
    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private CheckXpRewardsService service;

    @Test
    @DisplayName("Desbloquea rewards cuando el usuario supera el threshold")
    void checkAndUnlock_shouldUnlockRewards_whenXpSufficient() {
        Reward reward = Reward.builder()
                .id("reward-001")
                .name("Gold Title")
                .type(RewardType.TITLE)
                .xpThreshold(500)
                .active(true)
                .build();

        when(rewardRepository.findAllActive()).thenReturn(List.of(reward));

        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(1000)
                .weeklyXp(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();

        List<String> unlocked = service.checkAndUnlock(user);

        assertThat(unlocked).containsExactly("reward-001");
        assertThat(user.hasReward("reward-001")).isTrue();
        verify(userGamificationRepository).save(user);
    }

    @Test
    @DisplayName("No desbloquea rewards si el XP no alcanza el threshold")
    void checkAndUnlock_shouldNotUnlock_whenXpBelowThreshold() {
        Reward reward = Reward.builder()
                .id("reward-001")
                .name("Gold Title")
                .type(RewardType.TITLE)
                .xpThreshold(500)
                .active(true)
                .build();

        when(rewardRepository.findAllActive()).thenReturn(List.of(reward));

        UserGamification user = UserGamification.newUser("user-001");

        List<String> unlocked = service.checkAndUnlock(user);

        assertThat(unlocked).isEmpty();
        assertThat(user.hasReward("reward-001")).isFalse();
        verify(userGamificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("No desbloquea rewards ya obtenidos")
    void checkAndUnlock_shouldNotUnlock_whenAlreadyHasReward() {
        Reward reward = Reward.builder()
                .id("reward-001")
                .name("Gold Title")
                .type(RewardType.TITLE)
                .xpThreshold(500)
                .active(true)
                .build();

        when(rewardRepository.findAllActive()).thenReturn(List.of(reward));

        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(1000)
                .weeklyXp(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();

        service.checkAndUnlock(user);
        assertThat(user.hasReward("reward-001")).isTrue();

        List<String> unlocked = service.checkAndUnlock(user);

        assertThat(unlocked).isEmpty();
        verify(userGamificationRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Retorna lista vacía cuando no hay rewards activos")
    void checkAndUnlock_shouldReturnEmpty_whenNoActiveRewards() {
        when(rewardRepository.findAllActive()).thenReturn(List.of());

        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(9999)
                .weeklyXp(0)
                .rankingOptIn(false)
                .earnedMonas(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();

        List<String> unlocked = service.checkAndUnlock(user);

        assertThat(unlocked).isEmpty();
        verify(userGamificationRepository, never()).save(any());
    }
}
