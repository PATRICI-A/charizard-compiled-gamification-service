package com.charizad.compiled.gamification_service.infrastructure.config;

import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.MonaDocument;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.repository.MonaMongoRepository;
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
 * Solo se ejecuta si la colección 'Monas' está vacía y el perfil NO es 'prod' ni 'test'.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final MonaMongoRepository MonaRepository;
    private final UserGamificationMongoRepository userRepository;

    @Bean
    @Profile("!prod & !test")
    public CommandLineRunner seedData() {
        return args -> {
            try {
                if (MonaRepository.count() > 0) {
                    log.info("Catálogo de monas ya existe, omitiendo seeder.");
                    return;
                }
                log.info("Sembrando catálogo oficial de 13 monas (RF13.1)...");
                seedOfficialMonas();
                log.info("Catálogo de monas sembrado exitosamente.");
            } catch (Exception e) {
                log.warn("DataSeeder omitido — no se pudo conectar a MongoDB: {}", e.getMessage());
            }
        };
    }

    private void seedOfficialMonas() {
        List<MonaDocument> monas = List.of(
                Mona("Primera Conexión",
                        "Realizaste tu primera conexión con otro usuario en la plataforma.",
                        MonaCategory.COMMON, 100,
                        "https://cdn.example.com/monas/primera-conexion.png"),

                Mona("Conector",
                        "Acumulaste 5 conexiones activas.",
                        MonaCategory.UNCOMMON, 250,
                        "https://cdn.example.com/monas/conector.png"),

                Mona("Embajador Social",
                        "Acumulaste 10 conexiones activas.",
                        MonaCategory.RARE, 500,
                        "https://cdn.example.com/monas/embajador-social.png"),

                Mona("Primer Parche",
                        "Te uniste o creaste tu primer parche.",
                        MonaCategory.COMMON, 100,
                        "https://cdn.example.com/monas/primer-parche.png"),

                Mona("Anfitrión",
                        "Creaste 2 parches como capitán.",
                        MonaCategory.UNCOMMON, 250,
                        "https://cdn.example.com/monas/anfitrion.png"),

                Mona("Planificador",
                        "Creaste un parche con más de 3 días de anticipación.",
                        MonaCategory.COMMON, 100,
                        "https://cdn.example.com/monas/planificador.png"),

                Mona("Explorador I",
                        "Visitaste 3 zonas distintas del campus.",
                        MonaCategory.COMMON, 100,
                        "https://cdn.example.com/monas/explorador-i.png"),

                Mona("Explorador II",
                        "Visitaste 5 zonas distintas del campus.",
                        MonaCategory.UNCOMMON, 250,
                        "https://cdn.example.com/monas/explorador-ii.png"),

                Mona("Asistente",
                        "Asististe a un evento universitario institucional guardado.",
                        MonaCategory.RARE, 500,
                        "https://cdn.example.com/monas/asistente.png"),

                Mona("Primer Mensaje",
                        "Enviaste el primer mensaje en un parche recién creado.",
                        MonaCategory.COMMON, 100,
                        "https://cdn.example.com/monas/primer-mensaje.png"),

                Mona("Imán Social",
                        "Un nuevo usuario se unió a un parche que tú creaste.",
                        MonaCategory.COMMON, 100,
                        "https://cdn.example.com/monas/iman-social.png"),

                Mona("Meteoro Social",
                        "Pasaste de 0 a 10 conexiones en menos de 30 días desde tu registro.",
                        MonaCategory.EPIC, 1000,
                        "https://cdn.example.com/monas/meteoro-social.png"),

                Mona("Coleccionista",
                        "Desbloqueaste las 12 monas anteriores. ¡Eres una Leyenda del Parche!",
                        MonaCategory.LEGENDARY, 2000,
                        "https://cdn.example.com/monas/coleccionista.png")
        );

        MonaRepository.saveAll(monas);
        log.info("13 monas insertadas en el catálogo.");
    }

    private MonaDocument Mona(String name, String description,
                                MonaCategory category, int xpReward, String iconUrl) {
        return MonaDocument.builder()
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
