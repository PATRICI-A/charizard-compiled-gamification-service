package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.BadgeProgressResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserProgressServiceTest {

    private static final UUID BADGE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Mock private UserGamificationRepositoryPort userGamificationRepository;
    @Mock private BadgeRepositoryPort badgeRepository;
    @Mock private UserGamificationMapper userGamificationMapper;

    @InjectMocks
    private GetUserProgressService service;

    @Test
    @DisplayName("Returns one entry per active badge — existing progress mapped via mapper")
    void execute_shouldReturnOneEntryPerBadge_whenUserHasProgress() {
        BadgeProgress progress = BadgeProgress.builder()
                .badgeId(BADGE_ID)
                .currentValue(50)
                .requiredValue(100)
                .completed(false)
                .build();

        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(100).weeklyXp(50)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>(List.of(progress)))
                .build();

        Badge badge = Badge.builder()
                .id(BADGE_ID).name("Primer Parche")
                .description("desc").category(BadgeCategory.COMMON)
                .xpReward(10).active(true).createdAt(LocalDateTime.now())
                .build();

        BadgeProgressResponse expectedResponse = BadgeProgressResponse.builder()
                .badgeId(BADGE_ID).currentValue(50).requiredValue(100)
                .completed(false).percentageComplete(50)
                .build();

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllActive()).thenReturn(List.of(badge));
        when(userGamificationMapper.toProgressResponse(progress)).thenReturn(expectedResponse);

        List<BadgeProgressResponse> result = service.execute("user-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBadgeId()).isEqualTo(BADGE_ID);
        assertThat(result.get(0).getPercentageComplete()).isEqualTo(50);
    }

    @Test
    @DisplayName("Returns zero-progress entry for badges with no user activity")
    void execute_shouldReturnZeroProgress_whenUserHasNoProgress() {
        UserGamification user = UserGamification.builder()
                .userId("user-001")
                .totalXp(0).weeklyXp(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .build();

        Badge badge = Badge.builder()
                .id(BADGE_ID).name("Primera Conexión")
                .description("desc").category(BadgeCategory.COMMON)
                .xpReward(10).active(true).createdAt(LocalDateTime.now())
                .build();

        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllActive()).thenReturn(List.of(badge));

        List<BadgeProgressResponse> result = service.execute("user-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBadgeId()).isEqualTo(BADGE_ID);
        assertThat(result.get(0).getCurrentValue()).isEqualTo(0);
        assertThat(result.get(0).getRequiredValue()).isEqualTo(0);
        assertThat(result.get(0).isCompleted()).isFalse();
        assertThat(result.get(0).getPercentageComplete()).isEqualTo(0);
    }

    @Test
    @DisplayName("Throws exception when user not found")
    void execute_shouldThrow_whenUserNotFound() {
        when(userGamificationRepository.findByUserId("user-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("user-999"))
                .isInstanceOf(UserGamificationNotFoundException.class)
                .hasMessageContaining("user-999");
    }
}
