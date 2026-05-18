package com.charizad.compiled.gamification_service.domain.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RewardTypeTest {

    @Test
    @DisplayName("values contiene todas las constantes esperadas")
    void values_shouldContainAllConstants() {
        RewardType[] values = RewardType.values();
        assertThat(values).containsExactly(
                RewardType.TITLE,
                RewardType.FRAME,
                RewardType.COSMETIC,
                RewardType.FEATURE
        );
    }

    @Test
    @DisplayName("valueOf devuelve la constante correcta para cada nombre")
    void valueOf_shouldReturnCorrectConstant() {
        assertThat(RewardType.valueOf("TITLE")).isEqualTo(RewardType.TITLE);
        assertThat(RewardType.valueOf("FRAME")).isEqualTo(RewardType.FRAME);
        assertThat(RewardType.valueOf("COSMETIC")).isEqualTo(RewardType.COSMETIC);
        assertThat(RewardType.valueOf("FEATURE")).isEqualTo(RewardType.FEATURE);
    }
}
