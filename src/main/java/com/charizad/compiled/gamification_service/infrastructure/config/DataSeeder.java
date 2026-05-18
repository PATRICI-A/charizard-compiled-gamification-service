package com.charizad.compiled.gamification_service.infrastructure.config;

import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.BadgeDocument;
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

/**
 * Siembra el catálogo oficial de las 13 monas (RF13.1) al arrancar en entorno dev.
 * En producción las monas deben cargarse mediante un script de migración separado.
 *
 * Solo se ejecuta si la colección 'badges' está vacía y el perfil NO es 'prod' ni 'test'.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final BadgeMongoRepository badgeRepository;
    private final UserGamificationMongoRepository userRepository;

    @Bean
    @Profile("!prod & !test")
    public CommandLineRunner seedData() {
        return args -> {
            if (badgeRepository.count() > 0) {
                log.info("Catálogo de monas ya existe, omitiendo seeder.");
                return;
            }

            log.info("Sembrando catálogo oficial de 13 monas (RF13.1)...");
            seedOfficialMonas();
            log.info("Catálogo de monas sembrado exitosamente.");
        };
    }

    private void seedOfficialMonas() {
        List<BadgeDocument> monas = List.of(
                badge("Primera Conexión",
                        "Realizaste tu primera conexión con otro usuario en la plataforma.",
                        BadgeCategory.COMMON, 10,
                        "https://cdn.example.com/monas/primera-conexion.png"),

                badge("Conector",
                        "Acumulaste 5 conexiones activas.",
                        BadgeCategory.UNCOMMON, 25,
                        "https://cdn.example.com/monas/conector.png"),

                badge("Embajador Social",
                        "Acumulaste 10 conexiones activas.",
                        BadgeCategory.RARE, 50,
                        "https://cdn.example.com/monas/embajador-social.png"),

                badge("Primer Parche",
                        "Te uniste o creaste tu primer parche.",
                        BadgeCategory.COMMON, 10,
                        "https://cdn.example.com/monas/primer-parche.png"),

                badge("Anfitrión",
                        "Creaste 2 parches como capitán.",
                        BadgeCategory.UNCOMMON, 25,
                        "https://cdn.example.com/monas/anfitrion.png"),

                badge("Planificador",
                        "Creaste un parche con más de 3 días de anticipación.",
                        BadgeCategory.COMMON, 15,
                        "https://cdn.example.com/monas/planificador.png"),

                badge("Explorador I",
                        "Visitaste 3 zonas distintas del campus.",
                        BadgeCategory.COMMON, 15,
                        "https://cdn.example.com/monas/explorador-i.png"),

                badge("Explorador II",
                        "Visitaste 5 zonas distintas del campus.",
                        BadgeCategory.UNCOMMON, 30,
                        "https://cdn.example.com/monas/explorador-ii.png"),

                badge("Asistente",
                        "Asististe a un evento universitario institucional guardado.",
                        BadgeCategory.RARE, 50,
                        "https://cdn.example.com/monas/asistente.png"),

                badge("Primer Mensaje",
                        "Enviaste el primer mensaje en un parche recién creado.",
                        BadgeCategory.COMMON, 10,
                        "https://cdn.example.com/monas/primer-mensaje.png"),

                badge("Imán Social",
                        "Un nuevo usuario se unió a un parche que tú creaste.",
                        BadgeCategory.COMMON, 15,
                        "https://cdn.example.com/monas/iman-social.png"),

                badge("Meteoro Social",
                        "Pasaste de 0 a 10 conexiones en menos de 30 días desde tu registro.",
                        BadgeCategory.EPIC, 100,
                        "https://cdn.example.com/monas/meteoro-social.png"),

                badge("Coleccionista",
                        "Desbloqueaste las 12 monas anteriores. ¡Eres una Leyenda del Parche!",
                        BadgeCategory.LEGENDARY, 200,
                        "https://cdn.example.com/monas/coleccionista.png")
        );

        badgeRepository.saveAll(monas);
        log.info("13 monas insertadas en el catálogo.");
    }

    private BadgeDocument badge(String name, String description,
                                BadgeCategory category, int xpReward, String iconUrl) {
        return BadgeDocument.builder()
                .name(name)
                .description(description)
                .category(category)
                .xpReward(xpReward)
                .iconUrl(iconUrl)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }
}
