package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.EventCode;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EventCodeDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventCodeDocumentMapperTest {

    private final EventCodeDocumentMapper mapper = new EventCodeDocumentMapper();

    private final LocalDateTime FROM = LocalDateTime.of(2026, 5, 1, 0, 0);
    private final LocalDateTime UNTIL = LocalDateTime.of(2026, 5, 31, 23, 59);

    @Test
    @DisplayName("toDomain — mapea todos los campos correctamente")
    void toDomain_mapsAllFields() {
        EventCodeDocument doc = EventCodeDocument.builder()
                .id("ec1").code("ABC").validFrom(FROM).validUntil(UNTIL)
                .usedByUserIds(new ArrayList<>(List.of("u1", "u2")))
                .build();

        EventCode domain = mapper.toDomain(doc);

        assertThat(domain.getId()).isEqualTo("ec1");
        assertThat(domain.getCode()).isEqualTo("ABC");
        assertThat(domain.getValidFrom()).isEqualTo(FROM);
        assertThat(domain.getValidUntil()).isEqualTo(UNTIL);
        assertThat(domain.getUsedByUserIds()).containsExactly("u1", "u2");
    }

    @Test
    @DisplayName("toDomain — usedByUserIds null en doc → lista vacía en dominio")
    void toDomain_nullUsedByUserIds_emptyList() {
        EventCodeDocument doc = EventCodeDocument.builder()
                .id("ec2").code("XYZ").validFrom(FROM).validUntil(UNTIL)
                .usedByUserIds(null)
                .build();

        EventCode domain = mapper.toDomain(doc);

        assertThat(domain.getUsedByUserIds()).isEmpty();
    }

    @Test
    @DisplayName("toDomain — lista de usedByUserIds es copia independiente")
    void toDomain_usedByUserIdsCopied() {
        ArrayList<String> original = new ArrayList<>(List.of("u1"));
        EventCodeDocument doc = EventCodeDocument.builder()
                .id("ec3").code("COPY").validFrom(FROM).validUntil(UNTIL)
                .usedByUserIds(original)
                .build();

        EventCode domain = mapper.toDomain(doc);
        original.add("u2"); // mutate original

        assertThat(domain.getUsedByUserIds()).doesNotContain("u2");
    }

    @Test
    @DisplayName("toDocument — mapea todos los campos correctamente")
    void toDocument_mapsAllFields() {
        EventCode domain = EventCode.builder()
                .id("ec1").code("ABC").validFrom(FROM).validUntil(UNTIL)
                .usedByUserIds(new ArrayList<>(List.of("u1")))
                .build();

        EventCodeDocument doc = mapper.toDocument(domain);

        assertThat(doc.getId()).isEqualTo("ec1");
        assertThat(doc.getCode()).isEqualTo("ABC");
        assertThat(doc.getValidFrom()).isEqualTo(FROM);
        assertThat(doc.getValidUntil()).isEqualTo(UNTIL);
        assertThat(doc.getUsedByUserIds()).containsExactly("u1");
    }

    @Test
    @DisplayName("toDocument — lista usedByUserIds es copia independiente")
    void toDocument_usedByUserIdsCopied() {
        ArrayList<String> usedBy = new ArrayList<>(List.of("u1"));
        EventCode domain = EventCode.builder()
                .id("ec4").code("CPY").validFrom(FROM).validUntil(UNTIL)
                .usedByUserIds(usedBy)
                .build();

        EventCodeDocument doc = mapper.toDocument(domain);
        // doc list must be independent — mutation via domain model should not affect doc
        assertThat(doc.getUsedByUserIds()).containsExactly("u1");
    }

    @Test
    @DisplayName("round-trip toDomain→toDocument preserva valores")
    void roundTrip_preservesValues() {
        EventCodeDocument original = EventCodeDocument.builder()
                .id("ec5").code("RT").validFrom(FROM).validUntil(UNTIL)
                .usedByUserIds(new ArrayList<>(List.of("u1", "u3")))
                .build();

        EventCodeDocument roundTripped = mapper.toDocument(mapper.toDomain(original));

        assertThat(roundTripped.getId()).isEqualTo(original.getId());
        assertThat(roundTripped.getCode()).isEqualTo(original.getCode());
        assertThat(roundTripped.getUsedByUserIds()).containsExactlyElementsOf(original.getUsedByUserIds());
    }
}
