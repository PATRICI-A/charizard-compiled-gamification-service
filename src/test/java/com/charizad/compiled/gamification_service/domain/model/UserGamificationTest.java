package com.charizad.compiled.gamification_service.domain.model;

import com.charizad.compiled.gamification_service.domain.exceptions.BadgeAlreadyEarnedException;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class UserGamificationTest {

    private UserGamification user;
    private EarnedBadge badge;

    @BeforeEach
    void setUp() {
        user = UserGamification.newUser("user-001");
        badge = EarnedBadge.builder()
                .badgeId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .badgeName("Primer Parche")
                .earnedAt(LocalDateTime.now())
                .xpAwarded(100)
                .build();
    }

    @Test
    @DisplayName("newUser crea perfil con valores iniciales en cero")
    void newUser_shouldHaveZeroInitialValues() {
        assertThat(user.getTotalXp()).isZero();
        assertThat(user.getWeeklyXp()).isZero();
        assertThat(user.isRankingOptIn()).isFalse();
        assertThat(user.getEarnedBadges()).isEmpty();
        assertThat(user.getProgress()).isEmpty();
    }

    @Test
    @DisplayName("awardBadge suma XP total y semanal correctamente")
    void awardBadge_shouldAddXpToTotalAndWeekly() {
        user.awardBadge(badge);

        assertThat(user.getTotalXp()).isEqualTo(100);
        assertThat(user.getWeeklyXp()).isEqualTo(100);
        assertThat(user.getEarnedBadges()).hasSize(1);
    }

    @Test
    @DisplayName("awardBadge lanza excepción si el usuario ya tiene la insignia")
    void awardBadge_shouldThrow_whenBadgeAlreadyEarned() {
        user.awardBadge(badge);

        assertThatThrownBy(() -> user.awardBadge(badge))
                .isInstanceOf(BadgeAlreadyEarnedException.class)
                .hasMessageContaining("00000000-0000-0000-0000-000000000001");
    }

    @Test
    @DisplayName("hasBadge retorna true si el usuario posee la insignia")
    void hasBadge_shouldReturnTrue_whenBadgeIsOwned() {
        user.awardBadge(badge);
        assertThat(user.hasBadge(UUID.fromString("00000000-0000-0000-0000-000000000001"))).isTrue();
    }

    @Test
    @DisplayName("hasBadge retorna false si el usuario no posee la insignia")
    void hasBadge_shouldReturnFalse_whenBadgeNotOwned() {
        assertThat(user.hasBadge(UUID.fromString("00000000-0000-0000-0000-000000000999"))).isFalse();
    }

    @Test
    @DisplayName("resetWeeklyXp pone weeklyXp en cero pero conserva totalXp")
    void resetWeeklyXp_shouldZeroWeeklyXp_butKeepTotal() {
        user.awardBadge(badge);
        user.resetWeeklyXp();

        assertThat(user.getWeeklyXp()).isZero();
        assertThat(user.getTotalXp()).isEqualTo(100);
    }

    @Test
    @DisplayName("toggleRankingOptIn cambia el estado de opt-in")
    void toggleRankingOptIn_shouldFlipState() {
        assertThat(user.isRankingOptIn()).isFalse();

        user.toggleRankingOptIn();
        assertThat(user.isRankingOptIn()).isTrue();

        user.toggleRankingOptIn();
        assertThat(user.isRankingOptIn()).isFalse();
    }

    @Test
    @DisplayName("acumular múltiples insignias suma XP correctamente")
    void awardMultipleBadges_shouldAccumulateXp() {
        EarnedBadge badge2 = EarnedBadge.builder()
                .badgeId(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .badgeName("Parche Épico")
                .earnedAt(LocalDateTime.now())
                .xpAwarded(250)
                .build();

        user.awardBadge(badge);
        user.awardBadge(badge2);

        assertThat(user.getTotalXp()).isEqualTo(350);
        assertThat(user.getWeeklyXp()).isEqualTo(350);
        assertThat(user.getEarnedBadges()).hasSize(2);
    }

    @Test
    @DisplayName("getEarnedBadges retorna lista no modificable")
    void getEarnedBadges_shouldReturnUnmodifiableList() {
        user.awardBadge(badge);

        assertThatThrownBy(() -> user.getEarnedBadges().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
