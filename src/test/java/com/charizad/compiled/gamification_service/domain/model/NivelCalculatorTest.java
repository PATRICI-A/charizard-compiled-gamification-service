package com.charizad.compiled.gamification_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class NivelCalculatorTest {

    @ParameterizedTest(name = "{0} monas → nivel {1}")
    @CsvSource({
        "0,  1",
        "1,  1",
        "2,  1",
        "3,  2",
        "5,  2",
        "6,  3",
        "9,  3",
        "10, 4",
        "12, 4",
        "13, 5",
        "20, 5"
    })
    @DisplayName("getNivel retorna nivel correcto según monas")
    void getNivel_returnsCorrectLevel(int monas, int expectedNivel) {
        assertThat(NivelCalculator.getNivel(monas)).isEqualTo(expectedNivel);
    }

    @Test
    @DisplayName("getNivelName retorna null para todos los niveles (pendiente de definir)")
    void getNivelName_returnsNull() {
        for (int nivel = 1; nivel <= 5; nivel++) {
            assertThat(NivelCalculator.getNivelName(nivel)).isNull();
        }
    }

    @Test
    @DisplayName("getNivelNameByMonas retorna null")
    void getNivelNameByMonas_returnsNull() {
        assertThat(NivelCalculator.getNivelNameByMonas(5)).isNull();
        assertThat(NivelCalculator.getNivelNameByMonas(13)).isNull();
    }

    @ParameterizedTest(name = "{0} monas → {1} monas para siguiente nivel")
    @CsvSource({
        "0,  3",
        "1,  2",
        "2,  1",
        "3,  3",
        "5,  1",
        "6,  4",
        "9,  1",
        "10, 3",
        "12, 1",
        "13, 0",
        "20, 0"
    })
    @DisplayName("getMonasParaSiguienteNivel retorna valor correcto")
    void getMonasParaSiguienteNivel_returnsCorrect(int monas, int expected) {
        assertThat(NivelCalculator.getMonasParaSiguienteNivel(monas)).isEqualTo(expected);
    }
}
