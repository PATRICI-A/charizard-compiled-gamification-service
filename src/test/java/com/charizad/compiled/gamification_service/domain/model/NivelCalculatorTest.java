package com.charizad.compiled.gamification_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class NivelCalculatorTest {

    @ParameterizedTest(name = "{0} XP → nivel {1}")
    @CsvSource({
        "0,    1",
        "499,  1",
        "500,  2",
        "1499, 2",
        "1500, 3",
        "2999, 3",
        "3000, 4",
        "4999, 4",
        "5000, 5",
        "7000, 5"
    })
    @DisplayName("getNivel retorna nivel correcto según XP")
    void getNivel_returnsCorrectLevel(int xp, int expectedNivel) {
        assertThat(NivelCalculator.getNivel(xp)).isEqualTo(expectedNivel);
    }

    @ParameterizedTest(name = "nivel {0} → nombre {1}")
    @CsvSource({
        "1, Novato",
        "2, Explorador",
        "3, Conector",
        "4, Embajador",
        "5, Leyenda",
        "6, Leyenda"
    })
    @DisplayName("getNivelName retorna nombre correcto")
    void getNivelName_returnsCorrectName(int nivel, String expectedName) {
        assertThat(NivelCalculator.getNivelName(nivel)).isEqualTo(expectedName);
    }

    @ParameterizedTest(name = "{0} XP → {1} XP umbral siguiente nivel")
    @CsvSource({
        "0,    500",
        "499,  500",
        "500,  1500",
        "1499, 1500",
        "1500, 3000",
        "2999, 3000",
        "3000, 5000",
        "4999, 5000",
        "5000, 0",
        "7000, 0"
    })
    @DisplayName("getXpParaSiguienteNivel retorna valor correcto")
    void getXpParaSiguienteNivel_returnsCorrect(int xp, int expected) {
        assertThat(NivelCalculator.getXpParaSiguienteNivel(xp)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0} XP → {1} XP restante")
    @CsvSource({
        "0,    500",
        "100,  400",
        "500,  1000",
        "2000, 1000",
        "5000, 0"
    })
    @DisplayName("getXpRestante retorna valor correcto")
    void getXpRestante_returnsCorrect(int xp, int expected) {
        assertThat(NivelCalculator.getXpRestante(xp)).isEqualTo(expected);
    }
}
