package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.request.MonaUnlockEventRequest;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardMonaUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.CheckMonaUnlockUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Evaluates which monas (RF13.1) should be unlocked for a user based on an incoming action event.
 *
 * Mona names must match exactly what is seeded in DataSeeder.
 * Each Mona is only awarded once (idempotent — awardMona silently skips if already owned).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckMonaUnlockService implements CheckMonaUnlockUseCase {

    private static final String Mona_PRIMERA_CONEXION   = "Primera Conexión";
    private static final String Mona_CONECTOR           = "Conector";
    private static final String Mona_EMBAJADOR_SOCIAL   = "Embajador Social";
    private static final String Mona_PRIMER_PARCHE      = "Primer Parche";
    private static final String Mona_ANFITRION          = "Anfitrión";
    private static final String Mona_PLANIFICADOR       = "Planificador";
    private static final String Mona_EXPLORADOR_I       = "Explorador I";
    private static final String Mona_EXPLORADOR_II      = "Explorador II";
    private static final String Mona_ASISTENTE          = "Asistente";
    private static final String Mona_PRIMER_MENSAJE     = "Primer Mensaje";
    private static final String Mona_IMAN_SOCIAL        = "Imán Social";
    private static final String Mona_METEORO_SOCIAL     = "Meteoro Social";
    private static final String Mona_COLECCIONISTA      = "Coleccionista";

    private final AwardMonaUseCase awardMonaUseCase;
    private final MonaRepositoryPort monaRepository;
    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public List<String> execute(MonaUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();

        switch (event.getEventType()) {
            case CONNECTION_CREATED -> awarded.addAll(handleConnectionCreated(event));
            case PARCHE_JOINED_OR_CREATED -> awarded.addAll(handleParcheJoinedOrCreated(event));
            case MEMBER_JOINED_PARCHE -> awarded.addAll(handleMemberJoinedParche(event));
            case ZONE_VISITED -> awarded.addAll(handleZoneVisited(event));
            case INSTITUTIONAL_EVENT_ATTENDED -> awarded.addAll(handleEventAttended(event));
            case FIRST_MESSAGE_SENT -> awarded.addAll(handleFirstMessageSent(event));
            default -> log.warn("[CheckMonaUnlock] Unknown event type: {}", event.getEventType());
        }

        // After any award, check if Coleccionista is now unlocked
        if (!awarded.isEmpty()) {
            tryAwardColeccionista(event.getUserId(), awarded);
        }

        return awarded;
    }

    // ── CONNECTION_CREATED ────────────────────────────────────────────────────

    private List<String> handleConnectionCreated(MonaUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();
        int connections = event.getTotalActiveConnections() != null ? event.getTotalActiveConnections() : 0;

        if (connections >= 1) {
            tryAward(event.getUserId(), Mona_PRIMERA_CONEXION, awarded);
        }
        if (connections >= 5) {
            tryAward(event.getUserId(), Mona_CONECTOR, awarded);
        }
        if (connections >= 10) {
            tryAward(event.getUserId(), Mona_EMBAJADOR_SOCIAL, awarded);
            // Meteoro Social: 0 → 10 connections in less than 30 days from registration
            if (event.getUserRegisteredAt() != null) {
                long daysSinceRegistration = ChronoUnit.DAYS.between(
                        event.getUserRegisteredAt(), LocalDateTime.now());
                if (daysSinceRegistration < 30) {
                    tryAward(event.getUserId(), Mona_METEORO_SOCIAL, awarded);
                }
            }
        }
        return awarded;
    }

    // ── PARCHE_JOINED_OR_CREATED ──────────────────────────────────────────────

    private List<String> handleParcheJoinedOrCreated(MonaUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();

        // Any join or creation → Primer Parche
        tryAward(event.getUserId(), Mona_PRIMER_PARCHE, awarded);

        if (Boolean.TRUE.equals(event.getIsCreator())) {
            // Anfitrión: created 2 parches as captain
            int totalCreated = event.getTotalParchesCreated() != null ? event.getTotalParchesCreated() : 0;
            if (totalCreated >= 2) {
                tryAward(event.getUserId(), Mona_ANFITRION, awarded);
            }
            // Planificador: parche scheduled > 3 days ahead
            if (event.getParcheScheduledAt() != null) {
                long daysAhead = ChronoUnit.DAYS.between(LocalDateTime.now(), event.getParcheScheduledAt());
                if (daysAhead > 3) {
                    tryAward(event.getUserId(), Mona_PLANIFICADOR, awarded);
                }
            }
        }
        return awarded;
    }

    // ── MEMBER_JOINED_PARCHE ──────────────────────────────────────────────────

    private List<String> handleMemberJoinedParche(MonaUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();
        // The captain gets Imán Social when a new member joins their parche
        if (event.getCaptainUserId() != null) {
            tryAward(event.getCaptainUserId(), Mona_IMAN_SOCIAL, awarded);
        }
        return awarded;
    }

    // ── ZONE_VISITED ──────────────────────────────────────────────────────────

    private List<String> handleZoneVisited(MonaUnlockEventRequest event) {
        // RN-13.1.5: geo must be enabled; geo service sets this true on every published event
        if (!Boolean.TRUE.equals(event.getGeoLocationEnabled())) {
            log.debug("[CheckMonaUnlock] ZONE_VISITED ignorado — geoLocationEnabled=false para userId={}", event.getUserId());
            return List.of();
        }

        String campusZone = event.getCampusZone();
        if (campusZone == null || campusZone.isBlank()) {
            log.debug("[CheckMonaUnlock] ZONE_VISITED ignorado — campusZone vacío para userId={}", event.getUserId());
            return List.of();
        }

        String userId = event.getUserId();
        UserGamification user = userGamificationRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            log.debug("[CheckMonaUnlock] ZONE_VISITED ignorado — usuario {} no encontrado en gamificación", userId);
            return List.of();
        }

        user.visitZone(campusZone);
        int totalZones = user.getVisitedCampusZones().size();

        // Update progress display for Explorador I and II (RN-13.1.3)
        monaRepository.findByName(Mona_EXPLORADOR_I)
                .ifPresent(b -> user.updateProgress(b.getId(), totalZones, 3));
        monaRepository.findByName(Mona_EXPLORADOR_II)
                .ifPresent(b -> user.updateProgress(b.getId(), totalZones, 5));

        userGamificationRepository.save(user);

        List<String> awarded = new ArrayList<>();
        if (totalZones >= 3) tryAward(userId, Mona_EXPLORADOR_I, awarded);
        if (totalZones >= 5) tryAward(userId, Mona_EXPLORADOR_II, awarded);
        return awarded;
    }

    // ── INSTITUTIONAL_EVENT_ATTENDED ─────────────────────────────────────────

    private List<String> handleEventAttended(MonaUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();
        tryAward(event.getUserId(), Mona_ASISTENTE, awarded);
        return awarded;
    }

    // ── FIRST_MESSAGE_SENT ────────────────────────────────────────────────────

    private List<String> handleFirstMessageSent(MonaUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();
        tryAward(event.getUserId(), Mona_PRIMER_MENSAJE, awarded);
        return awarded;
    }

    // ── COLECCIONISTA ─────────────────────────────────────────────────────────

    private void tryAwardColeccionista(String userId, List<String> awarded) {
        UserGamification user = userGamificationRepository.findByUserId(userId).orElse(null);
        if (user == null) return;

        List<String> allMonaNames = List.of(
                Mona_PRIMERA_CONEXION, Mona_CONECTOR, Mona_EMBAJADOR_SOCIAL,
                Mona_PRIMER_PARCHE, Mona_ANFITRION, Mona_PLANIFICADOR,
                Mona_EXPLORADOR_I, Mona_EXPLORADOR_II, Mona_ASISTENTE,
                Mona_PRIMER_MENSAJE, Mona_IMAN_SOCIAL, Mona_METEORO_SOCIAL
        );

        boolean hasAll = allMonaNames.stream().allMatch(name ->
                user.getEarnedMonas().stream().anyMatch(b -> b.getMonaName().equals(name))
        );

        if (hasAll) {
            tryAward(userId, Mona_COLECCIONISTA, awarded);
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void tryAward(String userId, String MonaName, List<String> awarded) {
        Optional<Mona> Mona = monaRepository.findByName(MonaName);
        if (Mona.isEmpty()) {
            log.warn("[CheckMonaUnlock] Mona '{}' not found in catalog", MonaName);
            return;
        }
        try {
            awardMonaUseCase.execute(AwardMonaRequest.builder()
                    .userId(userId)
                    .monaId(Mona.get().getId())
                    .build());
            awarded.add(Mona.get().getId());
            log.info("[CheckMonaUnlock] Mona '{}' awarded to userId={}", MonaName, userId);
        } catch (Exception e) {
            // Mona already earned or other non-fatal exception — skip silently
            log.debug("[CheckMonaUnlock] Mona '{}' not awarded to userId={}: {}",
                    MonaName, userId, e.getMessage());
        }
    }
}
