package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.EventCode;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EventCodeDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.EventCodeDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.EventCodeMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventCodeRepositoryAdapterTest {

    @Mock private EventCodeMongoRepository mongoRepository;
    @Mock private EventCodeDocumentMapper mapper;

    @InjectMocks
    private EventCodeRepositoryAdapter adapter;

    private EventCode domainCode() {
        return EventCode.builder()
                .id("ec1").code("ABC")
                .validFrom(LocalDateTime.now().minusHours(1))
                .validUntil(LocalDateTime.now().plusHours(1))
                .usedByUserIds(new ArrayList<>())
                .build();
    }

    private EventCodeDocument document() {
        return EventCodeDocument.builder()
                .id("ec1").code("ABC")
                .validFrom(LocalDateTime.now().minusHours(1))
                .validUntil(LocalDateTime.now().plusHours(1))
                .usedByUserIds(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("findByCode — código encontrado retorna dominio")
    void findByCode_found_returnsDomain() {
        EventCodeDocument doc = document();
        EventCode domain = domainCode();
        when(mongoRepository.findByCode("ABC")).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        Optional<EventCode> result = adapter.findByCode("ABC");

        assertThat(result).contains(domain);
    }

    @Test
    @DisplayName("findByCode — código no encontrado retorna vacío")
    void findByCode_notFound_returnsEmpty() {
        when(mongoRepository.findByCode("NOPE")).thenReturn(Optional.empty());

        Optional<EventCode> result = adapter.findByCode("NOPE");

        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("save — persiste y retorna dominio")
    void save_persistsAndReturnsDomain() {
        EventCode domain = domainCode();
        EventCodeDocument doc = document();
        EventCode saved = domainCode();

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoRepository.save(doc)).thenReturn(doc);
        when(mapper.toDomain(doc)).thenReturn(saved);

        EventCode result = adapter.save(domain);

        assertThat(result).isSameAs(saved);
        verify(mongoRepository).save(doc);
    }
}
