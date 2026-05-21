package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingOptInResponse;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleRankingOptInServiceTest {

    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private ToggleRankingOptInService service;

    @Test
    @DisplayName("Enables ranking when participe=true")
    void execute_shouldEnableRanking_whenParticipeIsTrue() {
        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));
        when(userGamificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RankingOptInResponse result = service.execute("user-001", true);

        assertThat(result.isRankingOptIn()).isTrue();
        assertThat(result.getStudentId()).isEqualTo("user-001");
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Disables ranking when participe=false")
    void execute_shouldDisableRanking_whenParticipeIsFalse() {
        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(true)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));
        when(userGamificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RankingOptInResponse result = service.execute("user-001", false);

        assertThat(result.isRankingOptIn()).isFalse();
    }

    @Test
    @DisplayName("Creates new profile when user not found and enables ranking")
    void execute_shouldCreateNewProfile_whenUserNotFound() {
        when(userGamificationRepository.findByUserId("user-new")).thenReturn(Optional.empty());
        when(userGamificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RankingOptInResponse result = service.execute("user-new", true);

        assertThat(result.isRankingOptIn()).isTrue();
        assertThat(result.getStudentId()).isEqualTo("user-new");

        ArgumentCaptor<UserGamification> captor = ArgumentCaptor.forClass(UserGamification.class);
        verify(userGamificationRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo("user-new");
    }
}
