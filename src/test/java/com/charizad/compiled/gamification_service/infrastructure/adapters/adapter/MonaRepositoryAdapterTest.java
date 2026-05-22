package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.MonaDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.MonaDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.MonaMongoRepository;
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
class MonaRepositoryAdapterTest {

    @Mock private MonaMongoRepository mongoRepository;
    @Mock private MonaDocumentMapper mapper;

    @InjectMocks
    private MonaRepositoryAdapter adapter;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("save mapea, guarda y retorna el dominio")
    void save_shouldMapSaveAndReturnDomain() {
        Mona domain = Mona.builder().name("Test").build();
        MonaDocument doc = MonaDocument.builder().name("Test").build();
        MonaDocument savedDoc = MonaDocument.builder().id("Mona-001").name("Test").build();
        Mona savedDomain = Mona.builder().id("Mona-001").name("Test").build();

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoRepository.save(doc)).thenReturn(savedDoc);
        when(mapper.toDomain(savedDoc)).thenReturn(savedDomain);

        Mona result = adapter.save(domain);

        assertThat(result.getId()).isEqualTo("Mona-001");
        assertThat(result.getName()).isEqualTo("Test");
        verify(mapper).toDocument(domain);
        verify(mongoRepository).save(doc);
        verify(mapper).toDomain(savedDoc);
    }

    @Test
    @DisplayName("findById retorna Mona cuando existe")
    void findById_shouldReturnMona_whenFound() {
        MonaDocument doc = MonaDocument.builder().id("Mona-001").build();
        Mona domain = Mona.builder().id("Mona-001").build();

        when(mongoRepository.findById("Mona-001")).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        Optional<Mona> result = adapter.findById("Mona-001");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("Mona-001");
    }

    @Test
    @DisplayName("findById retorna empty cuando no existe")
    void findById_shouldReturnEmpty_whenNotFound() {
        when(mongoRepository.findById("Mona-999")).thenReturn(Optional.empty());

        Optional<Mona> result = adapter.findById("Mona-999");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAll retorna todas las insignias")
    void findAll_shouldReturnAllMonas() {
        MonaDocument doc1 = MonaDocument.builder().id("b1").name("A").build();
        MonaDocument doc2 = MonaDocument.builder().id("b2").name("B").build();
        Mona domain1 = Mona.builder().id("b1").name("A").build();
        Mona domain2 = Mona.builder().id("b2").name("B").build();

        when(mongoRepository.findAll()).thenReturn(List.of(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(domain1);
        when(mapper.toDomain(doc2)).thenReturn(domain2);

        List<Mona> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("b1");
        assertThat(result.get(1).getId()).isEqualTo("b2");
    }

    @Test
    @DisplayName("findAllActive retorna insignias activas")
    void findAllActive_shouldReturnActiveMonas() {
        MonaDocument doc = MonaDocument.builder().id("b1").active(true).build();
        Mona domain = Mona.builder().id("b1").active(true).build();

        when(mongoRepository.findByActiveTrue()).thenReturn(List.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        List<Mona> result = adapter.findAllActive();

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
