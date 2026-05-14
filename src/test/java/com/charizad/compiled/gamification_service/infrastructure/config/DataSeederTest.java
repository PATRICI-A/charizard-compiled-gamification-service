package com.charizad.compiled.gamification_service.infrastructure.config;

import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.BadgeMongoRepository;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.UserGamificationMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock private BadgeMongoRepository badgeRepository;
    @Mock private UserGamificationMongoRepository userRepository;

    @Test
    @DisplayName("seedData retorna CommandLineRunner no nulo")
    void seedData_shouldReturnCommandLineRunner() {
        DataSeeder dataSeeder = new DataSeeder(badgeRepository, userRepository);

        CommandLineRunner runner = dataSeeder.seedData();

        assertThat(runner).isNotNull();
    }

    @Test
    @DisplayName("skip seeding cuando la base de datos ya tiene datos")
    void seedData_shouldSkip_whenDatabaseHasData() throws Exception {
        when(badgeRepository.count()).thenReturn(5L);

        DataSeeder dataSeeder = new DataSeeder(badgeRepository, userRepository);
        CommandLineRunner runner = dataSeeder.seedData();

        runner.run();

        verify(badgeRepository).count();
        verifyNoMoreInteractions(badgeRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("seed datos cuando la base está vacía")
    void seedData_shouldInsertData_whenDatabaseEmpty() throws Exception {
        when(badgeRepository.count()).thenReturn(0L);
        when(badgeRepository.save(any(BadgeDocument.class)))
                .thenAnswer(inv -> {
                    BadgeDocument doc = inv.getArgument(0);
                    return BadgeDocument.builder()
                            .id("seeded-" + doc.getName().toLowerCase().replace(' ', '-'))
                            .name(doc.getName())
                            .description(doc.getDescription())
                            .category(doc.getCategory())
                            .xpReward(doc.getXpReward())
                            .iconUrl(doc.getIconUrl())
                            .createdAt(doc.getCreatedAt())
                            .active(doc.isActive())
                            .build();
                });

        DataSeeder dataSeeder = new DataSeeder(badgeRepository, userRepository);
        CommandLineRunner runner = dataSeeder.seedData();

        runner.run();

        verify(badgeRepository).count();
        verify(badgeRepository, times(4)).save(any(BadgeDocument.class));
        verify(userRepository, times(3)).save(any());
    }
}
