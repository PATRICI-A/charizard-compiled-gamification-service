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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
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
    @DisplayName("seed datos cuando la base está vacía inserta las 13 monas oficiales")
    void seedData_shouldInsertData_whenDatabaseEmpty() throws Exception {
        when(badgeRepository.count()).thenReturn(0L);
        when(badgeRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        DataSeeder dataSeeder = new DataSeeder(badgeRepository, userRepository);
        CommandLineRunner runner = dataSeeder.seedData();

        runner.run();

        verify(badgeRepository).count();
        verify(badgeRepository).saveAll(argThat(list -> {
            java.util.List<?> l = (java.util.List<?>) list;
            return l.size() == 13;
        }));
        verifyNoInteractions(userRepository);
    }
}
