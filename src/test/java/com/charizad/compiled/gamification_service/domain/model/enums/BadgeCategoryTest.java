package com.charizad.compiled.gamification_service.domain.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BadgeCategoryTest {

    @Test
    @DisplayName("values contiene todas las constantes esperadas")
    void values_shouldContainAllConstants() {
        BadgeCategory[] values = BadgeCategory.values();
        assertThat(values).containsExactly(
                BadgeCategory.COMMON,
                BadgeCategory.UNCOMMON,
                BadgeCategory.RARE,
                BadgeCategory.EPIC,
                BadgeCategory.LEGENDARY
        );
    }

    @Test
    @DisplayName("valueOf devuelve la constante correcta para cada nombre")
    void valueOf_shouldReturnCorrectConstant() {
        assertThat(BadgeCategory.valueOf("COMMON")).isEqualTo(BadgeCategory.COMMON);
        assertThat(BadgeCategory.valueOf("UNCOMMON")).isEqualTo(BadgeCategory.UNCOMMON);
        assertThat(BadgeCategory.valueOf("RARE")).isEqualTo(BadgeCategory.RARE);
        assertThat(BadgeCategory.valueOf("EPIC")).isEqualTo(BadgeCategory.EPIC);
        assertThat(BadgeCategory.valueOf("LEGENDARY")).isEqualTo(BadgeCategory.LEGENDARY);
    }

    @Test
    @DisplayName("ordinal incrementa según la rareza")
    void ordinal_shouldIncreaseWithRarity() {
        assertThat(BadgeCategory.COMMON.ordinal()).isEqualTo(0);
        assertThat(BadgeCategory.UNCOMMON.ordinal()).isEqualTo(1);
        assertThat(BadgeCategory.RARE.ordinal()).isEqualTo(2);
        assertThat(BadgeCategory.EPIC.ordinal()).isEqualTo(3);
        assertThat(BadgeCategory.LEGENDARY.ordinal()).isEqualTo(4);
    }
}
