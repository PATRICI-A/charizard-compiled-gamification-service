package com.charizad.compiled.gamification_service.infrastructure.adapters.adapter;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
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

import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGamificationRepositoryAdapterTest {

    @Mock private UserGamificationMongoRepository mongoRepository;
    @Mock private UserGamificationDocumentMapper mapper;

    @InjectMocks
    private UserGamificationRepositoryAdapter adapter;

    private static final UUID UG_ID_1 = UUID.fromString("10000000-0000-0000-0000-000000000001");

    @Test
    @DisplayName("save mapea, guarda y retorna el dominio")
    void save_shouldMapSaveAndReturnDomain() {
        UserGamification domain = UserGamification.builder().userId("user-001").build();
        UserGamificationDocument doc = UserGamificationDocument.builder().userId("user-001").build();
        UserGamificationDocument savedDoc = UserGamificationDocument.builder().id(UG_ID_1).userId("user-001").build();
        UserGamification savedDomain = UserGamification.builder().id(UG_ID_1).userId("user-001").build();

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoRepository.save(doc)).thenReturn(savedDoc);
        when(mapper.toDomain(savedDoc)).thenReturn(savedDomain);

        UserGamification result = adapter.save(domain);

        assertThat(result.getId()).isEqualTo(UG_ID_1);
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

    private UserGamification buildDomain(String userId, int weeklyMonas, int monthlyMonas, int semestralMonas, int badgeCount) {
        ArrayList<EarnedBadge> badges = new ArrayList<>();
        for (int i = 0; i < badgeCount; i++) {
            badges.add(EarnedBadge.builder()
                    .badgeId(UUID.randomUUID()).badgeName("Badge" + i).earnedAt(LocalDateTime.now()).xpAwarded(10).build());
        }
        return UserGamification.builder()
                .userId(userId).weeklyMonas(weeklyMonas).monthlyMonas(monthlyMonas).semestralMonas(semestralMonas)
                .rankingOptIn(true).earnedBadges(badges).progress(new ArrayList<>()).earnedRewards(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("findAllOptedInRankedFor con WEEKLY ordena por weeklyMonas descendente")
    void findAllOptedInRankedFor_weekly_shouldSortByWeeklyMonas() {
        UserGamificationDocument doc1 = UserGamificationDocument.builder().userId("user-a").build();
        UserGamificationDocument doc2 = UserGamificationDocument.builder().userId("user-b").build();
        UserGamification domain1 = buildDomain("user-a", 5, 0, 0, 2);
        UserGamification domain2 = buildDomain("user-b", 8, 0, 0, 1);

        when(mongoRepository.findByRankingOptInTrue()).thenReturn(List.of(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(domain1);
        when(mapper.toDomain(doc2)).thenReturn(domain2);

        List<UserGamification> result = adapter.findAllOptedInRankedFor(RankingType.WEEKLY);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo("user-b");
        assertThat(result.get(0).getWeeklyMonas()).isEqualTo(8);
        assertThat(result.get(1).getUserId()).isEqualTo("user-a");
        assertThat(result.get(1).getWeeklyMonas()).isEqualTo(5);
    }

    @Test
    @DisplayName("findAllOptedInRankedFor con MONTHLY ordena por monthlyMonas descendente")
    void findAllOptedInRankedFor_monthly_shouldSortByMonthlyMonas() {
        UserGamificationDocument doc1 = UserGamificationDocument.builder().userId("user-a").build();
        UserGamificationDocument doc2 = UserGamificationDocument.builder().userId("user-b").build();
        UserGamification domain1 = buildDomain("user-a", 0, 3, 0, 3);
        UserGamification domain2 = buildDomain("user-b", 0, 7, 0, 2);

        when(mongoRepository.findByRankingOptInTrue()).thenReturn(List.of(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(domain1);
        when(mapper.toDomain(doc2)).thenReturn(domain2);

        List<UserGamification> result = adapter.findAllOptedInRankedFor(RankingType.MONTHLY);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo("user-b");
        assertThat(result.get(0).getMonthlyMonas()).isEqualTo(7);
        assertThat(result.get(1).getUserId()).isEqualTo("user-a");
        assertThat(result.get(1).getMonthlyMonas()).isEqualTo(3);
    }

    @Test
    @DisplayName("findAllOptedInRankedFor con SEMESTER ordena por semestralMonas descendente")
    void findAllOptedInRankedFor_semester_shouldSortBySemestralMonas() {
        UserGamificationDocument doc1 = UserGamificationDocument.builder().userId("user-a").build();
        UserGamificationDocument doc2 = UserGamificationDocument.builder().userId("user-b").build();
        UserGamification domain1 = buildDomain("user-a", 0, 0, 10, 4);
        UserGamification domain2 = buildDomain("user-b", 0, 0, 15, 3);

        when(mongoRepository.findByRankingOptInTrue()).thenReturn(List.of(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(domain1);
        when(mapper.toDomain(doc2)).thenReturn(domain2);

        List<UserGamification> result = adapter.findAllOptedInRankedFor(RankingType.SEMESTER);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo("user-b");
        assertThat(result.get(0).getSemestralMonas()).isEqualTo(15);
        assertThat(result.get(1).getUserId()).isEqualTo("user-a");
        assertThat(result.get(1).getSemestralMonas()).isEqualTo(10);
    }

    @Test
    @DisplayName("findAllOptedInRankedFor con empate usa totalMonas como desempate")
    void findAllOptedInRankedFor_tie_shouldUseTotalMonas() {
        UserGamificationDocument doc1 = UserGamificationDocument.builder().userId("user-a").build();
        UserGamificationDocument doc2 = UserGamificationDocument.builder().userId("user-b").build();
        UserGamification domain1 = buildDomain("user-a", 5, 0, 0, 3);
        UserGamification domain2 = buildDomain("user-b", 5, 0, 0, 4);

        when(mongoRepository.findByRankingOptInTrue()).thenReturn(List.of(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(domain1);
        when(mapper.toDomain(doc2)).thenReturn(domain2);

        List<UserGamification> result = adapter.findAllOptedInRankedFor(RankingType.WEEKLY);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo("user-b");
        assertThat(result.get(1).getUserId()).isEqualTo("user-a");
    }

    @Test
    @DisplayName("countAllOptedIn retorna el conteo de usuarios opt-in")
    void countAllOptedIn_shouldReturnCount() {
        when(mongoRepository.countByRankingOptInTrue()).thenReturn(5L);

        long count = adapter.countAllOptedIn();

        assertThat(count).isEqualTo(5L);
    }
}
