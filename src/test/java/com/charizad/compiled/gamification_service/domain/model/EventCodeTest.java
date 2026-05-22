package com.charizad.compiled.gamification_service.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventCodeTest {

    private EventCode activeCode() {
        return EventCode.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-ec1000000001")).code("ABC123")
                .validFrom(LocalDateTime.now().minusHours(1))
                .validUntil(LocalDateTime.now().plusHours(1))
                .usedByUserIds(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("isValid — now dentro del rango retorna true")
    void isValid_withinRange_true() {
        assertThat(activeCode().isValid(LocalDateTime.now())).isTrue();
    }

    @Test
    @DisplayName("isValid — now antes de validFrom retorna false")
    void isValid_beforeValidFrom_false() {
        EventCode code = EventCode.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-ec0000000000")).code("X")
                .validFrom(LocalDateTime.now().plusHours(1))
                .validUntil(LocalDateTime.now().plusHours(2))
                .usedByUserIds(new ArrayList<>())
                .build();
        assertThat(code.isValid(LocalDateTime.now())).isFalse();
    }

    @Test
    @DisplayName("isValid — now después de validUntil retorna false")
    void isValid_afterValidUntil_false() {
        EventCode code = EventCode.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-ec0000000000")).code("X")
                .validFrom(LocalDateTime.now().minusHours(2))
                .validUntil(LocalDateTime.now().minusHours(1))
                .usedByUserIds(new ArrayList<>())
                .build();
        assertThat(code.isValid(LocalDateTime.now())).isFalse();
    }

    @Test
    @DisplayName("isValid — exactamente en validFrom retorna true (boundary)")
    void isValid_exactlyAtValidFrom_true() {
        LocalDateTime boundary = LocalDateTime.of(2026, 5, 19, 10, 0);
        EventCode code = EventCode.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-ec0000000000")).code("X")
                .validFrom(boundary)
                .validUntil(boundary.plusHours(1))
                .usedByUserIds(new ArrayList<>())
                .build();
        assertThat(code.isValid(boundary)).isTrue();
    }

    @Test
    @DisplayName("isUsedBy — usuario en lista retorna true")
    void isUsedBy_userPresent_true() {
        ArrayList<String> used = new ArrayList<>();
        used.add("u1");
        EventCode code = EventCode.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-ec0000000000")).code("X")
                .validFrom(LocalDateTime.now().minusHours(1))
                .validUntil(LocalDateTime.now().plusHours(1))
                .usedByUserIds(used)
                .build();
        assertThat(code.isUsedBy("u1")).isTrue();
    }

    @Test
    @DisplayName("isUsedBy — usuario ausente retorna false")
    void isUsedBy_userAbsent_false() {
        assertThat(activeCode().isUsedBy("unknown")).isFalse();
    }

    @Test
    @DisplayName("markUsedBy — agrega userId a lista")
    void markUsedBy_addsUser() {
        EventCode code = activeCode();
        code.markUsedBy("u1");
        assertThat(code.isUsedBy("u1")).isTrue();
    }

    @Test
    @DisplayName("getUsedByUserIds — retorna vista inmutable")
    void getUsedByUserIds_returnsUnmodifiable() {
        EventCode code = activeCode();
        assertThatThrownBy(() -> code.getUsedByUserIds().add("hacker"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("newCode factory — crea código con lista vacía")
    void newCode_createsWithEmptyList() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime until = from.plusDays(1);
        EventCode code = EventCode.newCode("CODE", from, until);

        assertThat(code.getCode()).isEqualTo("CODE");
        assertThat(code.getValidFrom()).isEqualTo(from);
        assertThat(code.getValidUntil()).isEqualTo(until);
        assertThat(code.getUsedByUserIds()).isEmpty();
        assertThat(code.getId()).isNull();
    }
}
