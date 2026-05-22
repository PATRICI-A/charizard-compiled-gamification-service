package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BadgeDocumentMapperTest {

    private static final UUID ID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID ID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID ID_3 = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private final BadgeDocumentMapper mapper = new BadgeDocumentMapper();

    @Test
    @DisplayName("toDomain mapea BadgeDocument a Badge correctamente")
    void toDomain_shouldMapDocumentToDomain() {
        LocalDateTime now = LocalDateTime.now();
        BadgeDocument doc = BadgeDocument.builder()
                .id(ID_1)
                .name("Legendario")
                .description("Insignia legendaria")
                .category(BadgeCategory.LEGENDARY)
                .xpReward(500)
                .iconUrl("http://example.com/icon.png")
                .createdAt(now)
                .active(true)
                .build();

        Badge result = mapper.toDomain(doc);

        assertThat(result.getId()).isEqualTo(ID_1);
        assertThat(result.getName()).isEqualTo("Legendario");
        assertThat(result.getDescription()).isEqualTo("Insignia legendaria");
        assertThat(result.getCategory()).isEqualTo(BadgeCategory.LEGENDARY);
        assertThat(result.getXpReward()).isEqualTo(500);
        assertThat(result.getIconUrl()).isEqualTo("http://example.com/icon.png");
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("toDocument mapea Badge a BadgeDocument correctamente")
    void toDocument_shouldMapDomainToDocument() {
        LocalDateTime now = LocalDateTime.now();
        Badge badge = Badge.builder()
                .id(ID_1)
                .name("Legendario")
                .description("Insignia legendaria")
                .category(BadgeCategory.LEGENDARY)
                .xpReward(500)
                .iconUrl("http://example.com/icon.png")
                .createdAt(now)
                .active(true)
                .build();

        BadgeDocument result = mapper.toDocument(badge);

        assertThat(result.getId()).isEqualTo(ID_1);
        assertThat(result.getName()).isEqualTo("Legendario");
        assertThat(result.getDescription()).isEqualTo("Insignia legendaria");
        assertThat(result.getCategory()).isEqualTo(BadgeCategory.LEGENDARY);
        assertThat(result.getXpReward()).isEqualTo(500);
        assertThat(result.getIconUrl()).isEqualTo("http://example.com/icon.png");
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("toDomain mapea BadgeDocument inactivo correctamente")
    void toDomain_shouldMapInactiveDocument() {
        BadgeDocument doc = BadgeDocument.builder()
                .id(ID_2)
                .name("Inactivo")
                .description("Inactiva")
                .category(BadgeCategory.RARE)
                .xpReward(200)
                .createdAt(LocalDateTime.now())
                .active(false)
                .build();

        Badge result = mapper.toDomain(doc);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    @DisplayName("toDocument mapea Badge inactivo correctamente")
    void toDocument_shouldMapInactiveBadge() {
        Badge badge = Badge.builder()
                .id(ID_2)
                .name("Inactivo")
                .description("Inactiva")
                .category(BadgeCategory.RARE)
                .xpReward(200)
                .createdAt(LocalDateTime.now())
                .active(false)
                .build();

        BadgeDocument result = mapper.toDocument(badge);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    @DisplayName("Mapeo bidireccional es consistente")
    void shouldBeBidirectionalConsistent() {
        LocalDateTime now = LocalDateTime.now();
        BadgeDocument original = BadgeDocument.builder()
                .id(ID_3)
                .name("Épica")
                .description("Insignia épica")
                .category(BadgeCategory.EPIC)
                .xpReward(1000)
                .iconUrl("http://example.com/epic.png")
                .createdAt(now)
                .active(true)
                .build();

        Badge domain = mapper.toDomain(original);
        BadgeDocument result = mapper.toDocument(domain);

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
