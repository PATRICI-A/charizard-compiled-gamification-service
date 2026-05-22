package com.charizad.compiled.gamification_service.domain.model;

import com.charizad.compiled.gamification_service.domain.exceptions.MonaAlreadyEarnedException;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedMona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class UserGamificationTest {

    private UserGamification user;
    private EarnedMona Mona;

    @BeforeEach
    void setUp() {
        user = UserGamification.newUser("user-001");
        Mona = EarnedMona.builder()
                .monaId("Mona-001")
                .monaName("Primer Parche")
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
        assertThat(user.getEarnedMonas()).isEmpty();
        assertThat(user.getProgress()).isEmpty();
    }

    @Test
    @DisplayName("awardMona suma XP total y semanal correctamente")
    void awardMona_shouldAddXpToTotalAndWeekly() {
        user.awardMona(Mona);

        assertThat(user.getTotalXp()).isEqualTo(100);
        assertThat(user.getWeeklyXp()).isEqualTo(100);
        assertThat(user.getEarnedMonas()).hasSize(1);
    }

    @Test
    @DisplayName("awardMona lanza excepción si el usuario ya tiene la insignia")
    void awardMona_shouldThrow_whenMonaAlreadyEarned() {
        user.awardMona(Mona);

        assertThatThrownBy(() -> user.awardMona(Mona))
                .isInstanceOf(MonaAlreadyEarnedException.class)
                .hasMessageContaining("Mona-001");
    }

    @Test
    @DisplayName("hasMona retorna true si el usuario posee la insignia")
    void hasMona_shouldReturnTrue_whenMonaIsOwned() {
        user.awardMona(Mona);
        assertThat(user.hasMona("Mona-001")).isTrue();
    }

    @Test
    @DisplayName("hasMona retorna false si el usuario no posee la insignia")
    void hasMona_shouldReturnFalse_whenMonaNotOwned() {
        assertThat(user.hasMona("Mona-999")).isFalse();
    }

    @Test
    @DisplayName("resetWeeklyStats pone weeklyXp y weeklyMonas en cero pero conserva totalXp")
    void resetWeeklyStats_shouldZeroWeeklyStats_butKeepTotal() {
        user.awardMona(Mona);
        user.resetWeeklyStats();

        assertThat(user.getWeeklyXp()).isZero();
        assertThat(user.getWeeklyMonas()).isZero();
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
    void awardMultipleMonas_shouldAccumulateXp() {
        EarnedMona Mona2 = EarnedMona.builder()
                .monaId("Mona-002")
                .monaName("Parche Épico")
                .earnedAt(LocalDateTime.now())
                .xpAwarded(250)
                .build();

        user.awardMona(Mona);
        user.awardMona(Mona2);

        assertThat(user.getTotalXp()).isEqualTo(350);
        assertThat(user.getWeeklyXp()).isEqualTo(350);
        assertThat(user.getEarnedMonas()).hasSize(2);
    }

    @Test
    @DisplayName("getEarnedMonas retorna lista no modificable")
    void getEarnedMonas_shouldReturnUnmodifiableList() {
        user.awardMona(Mona);

        assertThatThrownBy(() -> user.getEarnedMonas().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
