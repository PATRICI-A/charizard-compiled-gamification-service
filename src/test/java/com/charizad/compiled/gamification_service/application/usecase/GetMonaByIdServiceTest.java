package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.BadgeNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMonaByIdServiceTest {

    @Mock private BadgeRepositoryPort badgeRepository;
    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private GetMonaByIdService service;

    private Badge badge(String id, String name) {
        return Badge.builder().id(id).name(name).description("desc")
                .category(BadgeCategory.COMMON).active(true).build();
    }

    @Test
    @DisplayName("Badge no encontrado → BadgeNotFoundException")
    void execute_badgeNotFound_throws() {
        when(badgeRepository.findById("b-missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("u1", "b-missing"))
                .isInstanceOf(BadgeNotFoundException.class);
    }

    @Test
    @DisplayName("Badge encontrado, sin usuario → unlocked=false")
    void execute_noUser_unlockedFalse() {
        when(badgeRepository.findById("b1")).thenReturn(Optional.of(badge("b1", "Test")));
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        MonaResponse result = service.execute("u1", "b1");

        assertThat(result.getMonaId()).isEqualTo("b1");
        assertThat(result.isUnlocked()).isFalse();
    }

    @Test
    @DisplayName("Badge encontrado, usuario con mona ganada → unlocked=true")
    void execute_userEarnedBadge_unlockedTrue() {
        Badge b = badge("b1", "Primer Parche");
        when(badgeRepository.findById("b1")).thenReturn(Optional.of(b));

        LocalDateTime earned = LocalDateTime.of(2026, 5, 1, 12, 0);
        UserGamification user = UserGamification.builder()
                .userId("u1").totalXp(25).weeklyXp(0).weeklyMonas(1)
                .rankingOptIn(false)
                .earnedBadges(List.of(EarnedBadge.builder()
                        .badgeId("b1").badgeName("Primer Parche")
                        .earnedAt(earned).xpAwarded(25).build()))
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));

        MonaResponse result = service.execute("u1", "b1");

        assertThat(result.isUnlocked()).isTrue();
        assertThat(result.getEarnedAt()).isEqualTo(earned.toLocalDate());
        assertThat(result.getProgressPercentage()).isEqualTo(100.0f);
    }
}
