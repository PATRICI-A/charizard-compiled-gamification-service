package com.charizad.compiled.gamification_service.application.service;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.BadgeNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwardBadgeServiceTest {

    @Mock private BadgeRepositoryPort badgeRepository;
    @Mock private UserGamificationRepositoryPort userGamificationRepository;
    @Mock private NotificationEventPort notificationEventPort;
    @Mock private UserGamificationMapper userGamificationMapper;

    @InjectMocks
    private AwardBadgeService service;

    private Badge badge;
    private AwardBadgeRequest request;

    @BeforeEach
    void setUp() {
        badge = Badge.builder()
                .id("badge-001")
                .name("Primer Parche")
                .description("Asististe a tu primer parche")
                .category(BadgeCategory.COMMON)
                .xpReward(100)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        request = AwardBadgeRequest.builder()
                .userId("user-001")
                .badgeId("badge-001")
                .build();
    }

    @Test
    @DisplayName("Otorgar insignia a usuario nuevo crea perfil y suma XP")
    void execute_shouldCreateProfileAndAwardBadge_whenUserDoesNotExist() {
        when(badgeRepository.findById("badge-001")).thenReturn(Optional.of(badge));
        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.empty());
        when(userGamificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userGamificationMapper.toEarnedBadgeResponse(any())).thenReturn(
                EarnedBadgeResponse.builder().badgeId("badge-001").xpAwarded(100).build());

        EarnedBadgeResponse response = service.execute(request);

        assertThat(response.getBadgeId()).isEqualTo("badge-001");
        assertThat(response.getXpAwarded()).isEqualTo(100);
        verify(userGamificationRepository).save(any());
        verify(notificationEventPort).notifyBadgeEarned("user-001", badge);
    }

    @Test
    @DisplayName("Otorgar insignia ya poseída retorna la existente sin lanzar error (E1)")
    void execute_shouldReturnExistingBadge_whenUserAlreadyHasIt() {
        EarnedBadge alreadyOwned = EarnedBadge.builder()
                .badgeId("badge-001")
                .badgeName("Primer Parche")
                .earnedAt(LocalDateTime.now().minusDays(5))
                .xpAwarded(100)
                .build();

        ArrayList<EarnedBadge> earnedList = new ArrayList<>();
        earnedList.add(alreadyOwned);

        UserGamification userWithBadge = UserGamification.builder()
                .id("ug-001")
                .userId("user-001")
                .totalXp(100)
                .weeklyXp(100)
                .rankingOptIn(false)
                .earnedBadges(earnedList)
                .progress(new ArrayList<>())
                .build();

        when(badgeRepository.findById("badge-001")).thenReturn(Optional.of(badge));
        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(userWithBadge));
        when(userGamificationMapper.toEarnedBadgeResponse(alreadyOwned)).thenReturn(
                EarnedBadgeResponse.builder().badgeId("badge-001").xpAwarded(100).build());

        EarnedBadgeResponse response = service.execute(request);

        assertThat(response.getBadgeId()).isEqualTo("badge-001");
        verify(userGamificationRepository, never()).save(any());
        verify(notificationEventPort, never()).notifyBadgeEarned(any(), any());
    }

    @Test
    @DisplayName("Lanza BadgeNotFoundException si la insignia no existe")
    void execute_shouldThrow_whenBadgeNotFound() {
        when(badgeRepository.findById("badge-001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(BadgeNotFoundException.class)
                .hasMessageContaining("badge-001");

        verifyNoInteractions(userGamificationRepository);
        verifyNoInteractions(notificationEventPort);
    }

    @Test
    @DisplayName("Guarda el usuario con el XP correcto después de otorgar la insignia")
    void execute_shouldSaveUserWithCorrectXp() {
        when(badgeRepository.findById("badge-001")).thenReturn(Optional.of(badge));
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
