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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BadgeRepositoryAdapterTest {

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
        BadgeDocument savedDoc = BadgeDocument.builder().id("badge-001").name("Test").build();
        Badge savedDomain = Badge.builder().id("badge-001").name("Test").build();

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoRepository.save(doc)).thenReturn(savedDoc);
        when(mapper.toDomain(savedDoc)).thenReturn(savedDomain);

        Badge result = adapter.save(domain);

        assertThat(result.getId()).isEqualTo("badge-001");
        assertThat(result.getName()).isEqualTo("Test");
        verify(mapper).toDocument(domain);
        verify(mongoRepository).save(doc);
        verify(mapper).toDomain(savedDoc);
    }

    @Test
    @DisplayName("findById retorna badge cuando existe")
    void findById_shouldReturnBadge_whenFound() {
        BadgeDocument doc = BadgeDocument.builder().id("badge-001").build();
        Badge domain = Badge.builder().id("badge-001").build();

        when(mongoRepository.findById("badge-001")).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        Optional<Badge> result = adapter.findById("badge-001");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("badge-001");
    }

    @Test
    @DisplayName("findById retorna empty cuando no existe")
    void findById_shouldReturnEmpty_whenNotFound() {
        when(mongoRepository.findById("badge-999")).thenReturn(Optional.empty());

        Optional<Badge> result = adapter.findById("badge-999");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll retorna todas las insignias")
    void findAll_shouldReturnAllBadges() {
        BadgeDocument doc1 = BadgeDocument.builder().id("b1").name("A").build();
        BadgeDocument doc2 = BadgeDocument.builder().id("b2").name("B").build();
        Badge domain1 = Badge.builder().id("b1").name("A").build();
        Badge domain2 = Badge.builder().id("b2").name("B").build();

        when(mongoRepository.findAll()).thenReturn(List.of(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(domain1);
        when(mapper.toDomain(doc2)).thenReturn(domain2);

        List<Badge> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("b1");
        assertThat(result.get(1).getId()).isEqualTo("b2");
    }

    @Test
    @DisplayName("findAllActive retorna insignias activas")
    void findAllActive_shouldReturnActiveBadges() {
        BadgeDocument doc = BadgeDocument.builder().id("b1").active(true).build();
        Badge domain = Badge.builder().id("b1").active(true).build();

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
