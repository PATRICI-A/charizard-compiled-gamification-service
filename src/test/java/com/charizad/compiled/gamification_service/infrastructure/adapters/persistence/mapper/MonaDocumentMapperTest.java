package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.MonaDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MonaDocumentMapperTest {

    private final MonaDocumentMapper mapper = new MonaDocumentMapper();

    @Test
    @DisplayName("toDomain mapea MonaDocument a Mona correctamente")
    void toDomain_shouldMapDocumentToDomain() {
        LocalDateTime now = LocalDateTime.now();
        MonaDocument doc = MonaDocument.builder()
                .id("Mona-001")
                .name("Legendario")
                .description("Insignia legendaria")
                .category(MonaCategory.LEGENDARY)
                .xpReward(500)
                .iconUrl("http://example.com/icon.png")
                .createdAt(now)
                .active(true)
                .build();

        Mona result = mapper.toDomain(doc);

        assertThat(result.getId()).isEqualTo("Mona-001");
        assertThat(result.getName()).isEqualTo("Legendario");
        assertThat(result.getDescription()).isEqualTo("Insignia legendaria");
        assertThat(result.getCategory()).isEqualTo(MonaCategory.LEGENDARY);
        assertThat(result.getXpReward()).isEqualTo(500);
        assertThat(result.getIconUrl()).isEqualTo("http://example.com/icon.png");
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("toDocument mapea Mona a MonaDocument correctamente")
    void toDocument_shouldMapDomainToDocument() {
        LocalDateTime now = LocalDateTime.now();
        Mona mona = Mona.builder()
                .id("Mona-001")
                .name("Legendario")
                .description("Insignia legendaria")
                .category(MonaCategory.LEGENDARY)
                .xpReward(500)
                .iconUrl("http://example.com/icon.png")
                .createdAt(now)
                .active(true)
                .build();

        MonaDocument result = mapper.toDocument(mona);

        assertThat(result.getId()).isEqualTo("Mona-001");
        assertThat(result.getName()).isEqualTo("Legendario");
        assertThat(result.getDescription()).isEqualTo("Insignia legendaria");
        assertThat(result.getCategory()).isEqualTo(MonaCategory.LEGENDARY);
        assertThat(result.getXpReward()).isEqualTo(500);
        assertThat(result.getIconUrl()).isEqualTo("http://example.com/icon.png");
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("toDomain mapea MonaDocument inactivo correctamente")
    void toDomain_shouldMapInactiveDocument() {
        MonaDocument doc = MonaDocument.builder()
                .id("Mona-002")
                .name("Inactivo")
                .description("Inactiva")
                .category(MonaCategory.RARE)
                .xpReward(200)
                .createdAt(LocalDateTime.now())
                .active(false)
                .build();

        Mona result = mapper.toDomain(doc);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    @DisplayName("toDocument mapea Mona inactivo correctamente")
    void toDocument_shouldMapInactiveMona() {
        Mona mona = Mona.builder()
                .id("Mona-002")
                .name("Inactivo")
                .description("Inactiva")
                .category(MonaCategory.RARE)
                .xpReward(200)
                .createdAt(LocalDateTime.now())
                .active(false)
                .build();

        MonaDocument result = mapper.toDocument(mona);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    @DisplayName("Mapeo bidireccional es consistente")
    void shouldBeBidirectionalConsistent() {
        LocalDateTime now = LocalDateTime.now();
        MonaDocument original = MonaDocument.builder()
                .id("Mona-003")
                .name("Épica")
                .description("Insignia épica")
                .category(MonaCategory.EPIC)
                .xpReward(1000)
                .iconUrl("http://example.com/epic.png")
                .createdAt(now)
                .active(true)
                .build();

        Mona domain = mapper.toDomain(original);
        MonaDocument result = mapper.toDocument(domain);

        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getName()).isEqualTo(original.getName());
        assertThat(result.getDescription()).isEqualTo(original.getDescription());
        assertThat(result.getCategory()).isEqualTo(original.getCategory());
        assertThat(result.getXpReward()).isEqualTo(original.getXpReward());
        assertThat(result.getIconUrl()).isEqualTo(original.getIconUrl());
        assertThat(result.getCreatedAt()).isEqualTo(original.getCreatedAt());
        assertThat(result.isActive()).isEqualTo(original.isActive());
    }
}
