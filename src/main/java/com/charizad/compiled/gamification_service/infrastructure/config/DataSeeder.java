package com.charizad.compiled.gamification_service.infrastructure.config;

import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.EarnedBadgeSubdocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.UserGamificationDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.BadgeMongoRepository;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.UserGamificationMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final BadgeMongoRepository badgeRepository;
    private final UserGamificationMongoRepository userRepository;

    @Bean
    @Profile("!test")
    public CommandLineRunner seedData() {
        return args -> {
            if (badgeRepository.count() > 0) {
                log.info("Base de datos ya tiene datos, omitiendo seeder.");
                return;
            }

            log.info("Insertando datos de prueba...");

            // ── Insignias ────────────────────────────────────────────────────
            BadgeDocument badge1 = badgeRepository.save(BadgeDocument.builder()
                    .name("Primer Parche")
                    .description("Asististe a tu primer parche")
                    .category(BadgeCategory.COMMON)
                    .xpReward(50)
                    .iconUrl("https://cdn.example.com/badges/primer-parche.png")
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .build());

            BadgeDocument badge2 = badgeRepository.save(BadgeDocument.builder()
                    .name("Organizador Nato")
                    .description("Creaste 5 parches exitosos")
                    .category(BadgeCategory.UNCOMMON)
                    .xpReward(150)
                    .iconUrl("https://cdn.example.com/badges/organizador.png")
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .build());

            BadgeDocument badge3 = badgeRepository.save(BadgeDocument.builder()
                    .name("El Parche Épico")
                    .description("Asististe a un parche con más de 20 personas")
                    .category(BadgeCategory.EPIC)
                    .xpReward(300)
                    .iconUrl("https://cdn.example.com/badges/epico.png")
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .build());

            BadgeDocument badge4 = badgeRepository.save(BadgeDocument.builder()
                    .name("Leyenda del Campus")
                    .description("Acumulaste más de 1000 XP")
                    .category(BadgeCategory.LEGENDARY)
                    .xpReward(500)
                    .iconUrl("https://cdn.example.com/badges/leyenda.png")
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .build());

            log.info("Insignias creadas: {}, {}, {}, {}",
                    badge1.getId(), badge2.getId(), badge3.getId(), badge4.getId());

            // ── Usuarios de prueba ───────────────────────────────────────────
            userRepository.save(UserGamificationDocument.builder()
                    .userId("user-test-001")
                    .totalXp(200)
                    .weeklyXp(200)
                    .rankingOptIn(true)
                    .earnedBadges(List.of(
                            EarnedBadgeSubdocument.builder()
                                    .badgeId(badge1.getId())
                                    .badgeName(badge1.getName())
                                    .earnedAt(LocalDateTime.now().minusDays(10))
                                    .xpAwarded(badge1.getXpReward())
                                    .build(),
                            EarnedBadgeSubdocument.builder()
                                    .badgeId(badge2.getId())
                                    .badgeName(badge2.getName())
                                    .earnedAt(LocalDateTime.now().minusDays(3))
                                    .xpAwarded(badge2.getXpReward())
                                    .build()
                    ))
                    .progress(List.of())
                    .build());

            userRepository.save(UserGamificationDocument.builder()
                    .userId("user-test-002")
                    .totalXp(300)
                    .weeklyXp(300)
                    .rankingOptIn(true)
                    .earnedBadges(List.of(
                            EarnedBadgeSubdocument.builder()
                                    .badgeId(badge1.getId())
                                    .badgeName(badge1.getName())
                                    .earnedAt(LocalDateTime.now().minusDays(7))
                                    .xpAwarded(badge1.getXpReward())
                                    .build(),
                            EarnedBadgeSubdocument.builder()
                                    .badgeId(badge3.getId())
                                    .badgeName(badge3.getName())
                                    .earnedAt(LocalDateTime.now().minusDays(1))
                                    .xpAwarded(badge3.getXpReward())
                                    .build()
                    ))
                    .progress(List.of())
                    .build());

            userRepository.save(UserGamificationDocument.builder()
                    .userId("user-test-003")
                    .totalXp(50)
                    .weeklyXp(50)
                    .rankingOptIn(false)
                    .earnedBadges(List.of(
                            EarnedBadgeSubdocument.builder()
                                    .badgeId(badge1.getId())
                                    .badgeName(badge1.getName())
                                    .earnedAt(LocalDateTime.now().minusDays(2))
                                    .xpAwarded(badge1.getXpReward())
                                    .build()
                    ))
                    .progress(List.of())
                    .build());

            log.info("Usuarios de prueba creados: user-test-001, user-test-002, user-test-003");
            log.info("Seeder completado exitosamente.");
        };
    }
}
