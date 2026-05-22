package com.charizad.compiled.gamification_service.domain.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonaCategoryTest {

    @Test
    @DisplayName("values contiene todas las constantes esperadas")
    void values_shouldContainAllConstants() {
        MonaCategory[] values = MonaCategory.values();
        assertThat(values).containsExactly(
                MonaCategory.COMMON,
                MonaCategory.UNCOMMON,
                MonaCategory.RARE,
                MonaCategory.EPIC,
                MonaCategory.LEGENDARY
        );
    }

    @Test
    @DisplayName("valueOf devuelve la constante correcta para cada nombre")
    void valueOf_shouldReturnCorrectConstant() {
        assertThat(MonaCategory.valueOf("COMMON")).isEqualTo(MonaCategory.COMMON);
        assertThat(MonaCategory.valueOf("UNCOMMON")).isEqualTo(MonaCategory.UNCOMMON);
        assertThat(MonaCategory.valueOf("RARE")).isEqualTo(MonaCategory.RARE);
        assertThat(MonaCategory.valueOf("EPIC")).isEqualTo(MonaCategory.EPIC);
        assertThat(MonaCategory.valueOf("LEGENDARY")).isEqualTo(MonaCategory.LEGENDARY);
    }

    @Test
    @DisplayName("ordinal incrementa según la rareza")
    void ordinal_shouldIncreaseWithRarity() {
        assertThat(MonaCategory.COMMON.ordinal()).isEqualTo(0);
        assertThat(MonaCategory.UNCOMMON.ordinal()).isEqualTo(1);
        assertThat(MonaCategory.RARE.ordinal()).isEqualTo(2);
        assertThat(MonaCategory.EPIC.ordinal()).isEqualTo(3);
        assertThat(MonaCategory.LEGENDARY.ordinal()).isEqualTo(4);
    }
}
