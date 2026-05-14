package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.UserGamificationDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.UserGamificationDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.UserGamificationMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGamificationRepositoryAdapterTest {

    @Mock private UserGamificationMongoRepository mongoRepository;
    @Mock private UserGamificationDocumentMapper mapper;

    @InjectMocks
    private UserGamificationRepositoryAdapter adapter;

    @Test
    @DisplayName("save mapea, guarda y retorna el dominio")
    void save_shouldMapSaveAndReturnDomain() {
        UserGamification domain = UserGamification.builder().userId("user-001").build();
        UserGamificationDocument doc = UserGamificationDocument.builder().userId("user-001").build();
        UserGamificationDocument savedDoc = UserGamificationDocument.builder().id("ug-001").userId("user-001").build();
        UserGamification savedDomain = UserGamification.builder().id("ug-001").userId("user-001").build();

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoRepository.save(doc)).thenReturn(savedDoc);
        when(mapper.toDomain(savedDoc)).thenReturn(savedDomain);

        UserGamification result = adapter.save(domain);

        assertThat(result.getId()).isEqualTo("ug-001");
        assertThat(result.getUserId()).isEqualTo("user-001");
    }

    @Test
    @DisplayName("findByUserId retorna usuario cuando existe")
    void findByUserId_shouldReturnUser_whenFound() {
        UserGamificationDocument doc = UserGamificationDocument.builder().userId("user-001").build();
        UserGamification domain = UserGamification.builder().userId("user-001").build();

        when(mongoRepository.findByUserId("user-001")).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        Optional<UserGamification> result = adapter.findByUserId("user-001");

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo("user-001");
    }

    @Test
    @DisplayName("findByUserId retorna empty cuando no existe")
    void findByUserId_shouldReturnEmpty_whenNotFound() {
        when(mongoRepository.findByUserId("user-999")).thenReturn(Optional.empty());

        Optional<UserGamification> result = adapter.findByUserId("user-999");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllOptedInOrderByWeeklyXpDesc usa PageRequest y retorna lista")
    void findAllOptedInOrderByWeeklyXpDesc_shouldReturnOptedInUsers() {
        UserGamificationDocument doc = UserGamificationDocument.builder().userId("user-001").rankingOptIn(true).build();
        UserGamification domain = UserGamification.builder().userId("user-001").rankingOptIn(true).build();

        when(mongoRepository.findByRankingOptInTrueOrderByWeeklyXpDesc(PageRequest.of(0, 10)))
                .thenReturn(List.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        List<UserGamification> result = adapter.findAllOptedInOrderByWeeklyXpDesc(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user-001");
    }

    @Test
    @DisplayName("findAllOptedIn retorna usuarios con ranking opt-in")
    void findAllOptedIn_shouldReturnOptedInUsers() {
        UserGamificationDocument doc = UserGamificationDocument.builder().userId("user-001").rankingOptIn(true).build();
        UserGamification domain = UserGamification.builder().userId("user-001").rankingOptIn(true).build();

        when(mongoRepository.findByRankingOptInTrue()).thenReturn(List.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        List<UserGamification> result = adapter.findAllOptedIn();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo("user-001");
    }

    @Test
    @DisplayName("findAllOptedIn retorna lista vacía si no hay opt-in")
    void findAllOptedIn_shouldReturnEmpty_whenNoOptIn() {
        when(mongoRepository.findByRankingOptInTrue()).thenReturn(List.of());

        List<UserGamification> result = adapter.findAllOptedIn();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("saveAll mapea y guarda múltiples usuarios")
    void saveAll_shouldMapAndSaveMultipleUsers() {
        UserGamification user1 = UserGamification.builder().userId("user-001").build();
        UserGamification user2 = UserGamification.builder().userId("user-002").build();
        UserGamificationDocument doc1 = UserGamificationDocument.builder().userId("user-001").build();
        UserGamificationDocument doc2 = UserGamificationDocument.builder().userId("user-002").build();

        when(mapper.toDocument(user1)).thenReturn(doc1);
        when(mapper.toDocument(user2)).thenReturn(doc2);

        adapter.saveAll(List.of(user1, user2));

        ArgumentCaptor<List<UserGamificationDocument>> captor = ArgumentCaptor.forClass(List.class);
        verify(mongoRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }
}
