package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.Reward;
import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.RewardDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper.RewardDocumentMapper;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.RewardMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardRepositoryAdapterTest {

    @Mock private RewardMongoRepository mongoRepository;
    @Mock private RewardDocumentMapper mapper;

    @InjectMocks
    private RewardRepositoryAdapter adapter;

    private Reward domainReward(String id) {
        return Reward.builder().id(id).name("R" + id).type(RewardType.TITLE).xpThreshold(100).active(true).build();
    }

    private RewardDocument docReward(String id) {
        return RewardDocument.builder().id(id).name("R" + id).type(RewardType.TITLE).xpThreshold(100).active(true).build();
    }

    @Test
    @DisplayName("save mapea, guarda y retorna dominio")
    void save_shouldMapSaveAndReturnDomain() {
        Reward domain = domainReward(null);
        RewardDocument doc = docReward(null);
        RewardDocument savedDoc = docReward("r1");
        Reward savedDomain = domainReward("r1");

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoRepository.save(doc)).thenReturn(savedDoc);
        when(mapper.toDomain(savedDoc)).thenReturn(savedDomain);

        Reward result = adapter.save(domain);

        assertThat(result.getId()).isEqualTo("r1");
        verify(mapper).toDocument(domain);
        verify(mongoRepository).save(doc);
        verify(mapper).toDomain(savedDoc);
    }

    @Test
    @DisplayName("findById retorna reward cuando existe")
    void findById_returnsReward_whenFound() {
        RewardDocument doc = docReward("r1");
        Reward domain = domainReward("r1");

        when(mongoRepository.findById("r1")).thenReturn(Optional.of(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        Optional<Reward> result = adapter.findById("r1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("r1");
    }

    @Test
    @DisplayName("findById retorna empty cuando no existe")
    void findById_returnsEmpty_whenNotFound() {
        when(mongoRepository.findById("r999")).thenReturn(Optional.empty());

        Optional<Reward> result = adapter.findById("r999");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findAllActive retorna rewards activos ordenados por xpThreshold")
    void findAllActive_returnsActiveSortedRewards() {
        RewardDocument doc1 = docReward("r1");
        RewardDocument doc2 = docReward("r2");
        Reward domain1 = domainReward("r1");
        Reward domain2 = domainReward("r2");

        when(mongoRepository.findByActiveTrueOrderByXpThresholdAsc()).thenReturn(List.of(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(domain1);
        when(mapper.toDomain(doc2)).thenReturn(domain2);

        List<Reward> result = adapter.findAllActive();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("r1");
        assertThat(result.get(1).getId()).isEqualTo("r2");
    }

    @Test
    @DisplayName("findAll retorna todos los rewards")
    void findAll_returnsAllRewards() {
        RewardDocument doc1 = docReward("r1");
        RewardDocument doc2 = docReward("r2");
        Reward domain1 = domainReward("r1");
        Reward domain2 = domainReward("r2");

        when(mongoRepository.findAll()).thenReturn(List.of(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(domain1);
        when(mapper.toDomain(doc2)).thenReturn(domain2);

        List<Reward> result = adapter.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findAllActive retorna lista vacía cuando no hay activos")
    void findAllActive_returnsEmpty_whenNone() {
        when(mongoRepository.findByActiveTrueOrderByXpThresholdAsc()).thenReturn(List.of());

        List<Reward> result = adapter.findAllActive();

        assertThat(result).isEmpty();
    }
}
