package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.BadgeDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.BadgeMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeRepositoryAdapterTest {

    private static final UUID ID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID ID_B1 = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final UUID ID_B2 = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID ID_999 = UUID.fromString("00000000-0000-0000-0000-000000000999");

    @Mock private BadgeMongoRepository mongoRepository;
    @Mock private BadgeDocumentMapper mapper;

    @InjectMocks
    private BadgeRepositoryAdapter adapter;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("save mapea, guarda y retorna el dominio")
    void save_shouldMapSaveAndReturnDomain() {
        Badge domain = Badge.builder().name("Test").build();
        BadgeDocument doc = BadgeDocument.builder().name("Test").build();
        BadgeDocument savedDoc = BadgeDocument.builder().id(ID_1).name("Test").build();
        Badge savedDomain = Badge.builder().id(ID_1).name("Test").build();

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoRepository.save(doc)).thenReturn(savedDoc);
        when(mapper.toDomain(savedDoc)).thenReturn(savedDomain);

        Badge result = adapter.save(domain);

        assertThat(result.getId()).isEqualTo(ID_1);
        assertThat(result.getName()).isEqualTo("Test");
        verify(mapper).toDocument(domain);
        verify(mongoRepository).save(doc);
        verify(mapper).toDomain(savedDoc);
    }

    @Test
    @DisplayName("findById retorna badge cuando existe")
    void findById_shouldReturnBadge_whenFound() {
        BadgeDocument doc = BadgeDocument.builder().id(ID_1).build();
        Badge domain = Badge.builder().id(ID_1).build();

        when(mongoRepository.findById(ID_1)).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        Optional<Badge> result = adapter.findById(ID_1);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(ID_1);
    }

    @Test
    @DisplayName("findById retorna empty cuando no existe")
    void findById_shouldReturnEmpty_whenNotFound() {
        when(mongoRepository.findById(ID_999)).thenReturn(Optional.empty());

        Optional<Badge> result = adapter.findById(ID_999);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll retorna todas las insignias")
    void findAll_shouldReturnAllBadges() {
        BadgeDocument doc1 = BadgeDocument.builder().id(ID_B1).name("A").build();
        BadgeDocument doc2 = BadgeDocument.builder().id(ID_B2).name("B").build();
        Badge domain1 = Badge.builder().id(ID_B1).name("A").build();
        Badge domain2 = Badge.builder().id(ID_B2).name("B").build();

        when(mongoRepository.findAll()).thenReturn(List.of(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(domain1);
        when(mapper.toDomain(doc2)).thenReturn(domain2);

        List<Badge> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(ID_B1);
        assertThat(result.get(1).getId()).isEqualTo(ID_B2);
    }

    @Test
    @DisplayName("findAllActive retorna insignias activas")
    void findAllActive_shouldReturnActiveBadges() {
        BadgeDocument doc = BadgeDocument.builder().id(ID_B1).active(true).build();
        Badge domain = Badge.builder().id(ID_B1).active(true).build();

        when(mongoRepository.findByActiveTrue()).thenReturn(List.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        List<Badge> result = adapter.findAllActive();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    @DisplayName("existsByName retorna true cuando existe")
    void existsByName_shouldReturnTrue_whenExists() {
        when(mongoRepository.existsByName("Test")).thenReturn(true);

        boolean result = adapter.existsByName("Test");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByName retorna false cuando no existe")
    void existsByName_shouldReturnFalse_whenNotExists() {
        when(mongoRepository.existsByName("NotFound")).thenReturn(false);

        boolean result = adapter.existsByName("NotFound");

        assertThat(result).isFalse();
    }
}
