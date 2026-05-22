package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.BadgeNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.domain.ports.in.CheckXpRewardsUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.NotificationEventPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwardBadgeServiceTest {

    private static final UUID BADGE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID UG_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");

    @Mock private BadgeRepositoryPort badgeRepository;
    @Mock private UserGamificationRepositoryPort userGamificationRepository;
    @Mock private NotificationEventPort notificationEventPort;
    @Mock private UserGamificationMapper userGamificationMapper;
    @Mock private CheckXpRewardsUseCase checkXpRewardsUseCase;

    @InjectMocks
    private AwardBadgeService service;

    private Badge badge;
    private AwardBadgeRequest request;

    @BeforeEach
    void setUp() {
        badge = Badge.builder()
                .id(BADGE_ID)
                .name("Primer Parche")
                .description("Asististe a tu primer parche")
                .category(BadgeCategory.COMMON)
                .xpReward(100)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        request = AwardBadgeRequest.builder()
                .userId("user-001")
                .badgeId(BADGE_ID)
                .build();
    }

    @Test
    @DisplayName("Otorgar insignia ya poseída retorna la existente sin lanzar error (E1)")
    void execute_shouldReturnExistingBadge_whenUserAlreadyHasIt() {
        EarnedBadge alreadyOwned = EarnedBadge.builder()
                .badgeId(BADGE_ID)
                .badgeName("Primer Parche")
                .earnedAt(LocalDateTime.now().minusDays(5))
                .xpAwarded(100)
                .build();

        ArrayList<EarnedBadge> earnedList = new ArrayList<>();
        earnedList.add(alreadyOwned);

        UserGamification userWithBadge = UserGamification.builder()
                .id(UG_ID)
                .userId("user-001")
                .totalXp(100)
                .weeklyXp(100)
                .rankingOptIn(false)
                .earnedBadges(earnedList)
                .progress(new ArrayList<>())
                .build();

        when(badgeRepository.findById(BADGE_ID)).thenReturn(Optional.of(badge));
        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(userWithBadge));
        when(userGamificationMapper.toEarnedBadgeResponse(alreadyOwned)).thenReturn(
                EarnedBadgeResponse.builder().badgeId(BADGE_ID).xpAwarded(100).build());

        EarnedBadgeResponse response = service.execute(request);

        assertThat(response.getBadgeId()).isEqualTo(BADGE_ID);
        verify(userGamificationRepository, never()).save(any());
        verify(notificationEventPort, never()).notifyBadgeEarned(any(), any());
    }

    @Test
    @DisplayName("Lanza BadgeNotFoundException si la insignia no existe")
    void execute_shouldThrow_whenBadgeNotFound() {
        when(badgeRepository.findById(BADGE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(BadgeNotFoundException.class);

        verifyNoInteractions(userGamificationRepository);
        verifyNoInteractions(notificationEventPort);
    }

    @Test
    @DisplayName("Guarda el usuario con el XP correcto después de otorgar la insignia")
    void execute_shouldSaveUserWithCorrectXp() {
        when(badgeRepository.findById(BADGE_ID)).thenReturn(Optional.of(badge));
        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.empty());
        when(userGamificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userGamificationMapper.toEarnedBadgeResponse(any())).thenReturn(new EarnedBadgeResponse());

        service.execute(request);

        ArgumentCaptor<UserGamification> captor = ArgumentCaptor.forClass(UserGamification.class);
        verify(userGamificationRepository).save(captor.capture());

        UserGamification saved = captor.getValue();
        assertThat(saved.getTotalXp()).isEqualTo(100);
        assertThat(saved.getWeeklyXp()).isEqualTo(100);
        assertThat(saved.getEarnedBadges()).hasSize(1);
    }
}
