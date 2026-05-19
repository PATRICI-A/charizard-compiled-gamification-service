package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.request.BadgeUnlockEventRequest;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardBadgeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.CheckBadgeUnlockUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
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
 * Badge names must match exactly what is seeded in DataSeeder.
 * Each badge is only awarded once (idempotent — awardBadge silently skips if already owned).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckBadgeUnlockService implements CheckBadgeUnlockUseCase {

    private static final String BADGE_PRIMERA_CONEXION   = "Primera Conexión";
    private static final String BADGE_CONECTOR           = "Conector";
    private static final String BADGE_EMBAJADOR_SOCIAL   = "Embajador Social";
    private static final String BADGE_PRIMER_PARCHE      = "Primer Parche";
    private static final String BADGE_ANFITRION          = "Anfitrión";
    private static final String BADGE_PLANIFICADOR       = "Planificador";
    private static final String BADGE_EXPLORADOR_I       = "Explorador I";
    private static final String BADGE_EXPLORADOR_II      = "Explorador II";
    private static final String BADGE_ASISTENTE          = "Asistente";
    private static final String BADGE_PRIMER_MENSAJE     = "Primer Mensaje";
    private static final String BADGE_IMAN_SOCIAL        = "Imán Social";
    private static final String BADGE_METEORO_SOCIAL     = "Meteoro Social";
    private static final String BADGE_COLECCIONISTA      = "Coleccionista";

    private final AwardBadgeUseCase awardBadgeUseCase;
    private final BadgeRepositoryPort badgeRepository;
    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public List<String> execute(BadgeUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();

        switch (event.getEventType()) {
            case CONNECTION_CREATED -> awarded.addAll(handleConnectionCreated(event));
            case PARCHE_JOINED_OR_CREATED -> awarded.addAll(handleParcheJoinedOrCreated(event));
            case MEMBER_JOINED_PARCHE -> awarded.addAll(handleMemberJoinedParche(event));
            case ZONE_VISITED -> awarded.addAll(handleZoneVisited(event));
            case INSTITUTIONAL_EVENT_ATTENDED -> awarded.addAll(handleEventAttended(event));
            case FIRST_MESSAGE_SENT -> awarded.addAll(handleFirstMessageSent(event));
            default -> log.warn("[CheckBadgeUnlock] Unknown event type: {}", event.getEventType());
        }

        // After any award, check if Coleccionista is now unlocked
        if (!awarded.isEmpty()) {
            tryAwardColeccionista(event.getUserId(), awarded);
        }

        return awarded;
    }

    // ── CONNECTION_CREATED ────────────────────────────────────────────────────

    private List<String> handleConnectionCreated(BadgeUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();
        int connections = event.getTotalActiveConnections() != null ? event.getTotalActiveConnections() : 0;

        if (connections >= 1) {
            tryAward(event.getUserId(), BADGE_PRIMERA_CONEXION, awarded);
        }
        if (connections >= 5) {
            tryAward(event.getUserId(), BADGE_CONECTOR, awarded);
        }
        if (connections >= 10) {
            tryAward(event.getUserId(), BADGE_EMBAJADOR_SOCIAL, awarded);
            // Meteoro Social: 0 → 10 connections in less than 30 days from registration
            if (event.getUserRegisteredAt() != null) {
                long daysSinceRegistration = ChronoUnit.DAYS.between(
                        event.getUserRegisteredAt(), LocalDateTime.now());
                if (daysSinceRegistration < 30) {
                    tryAward(event.getUserId(), BADGE_METEORO_SOCIAL, awarded);
                }
            }
        }
        return awarded;
    }

    // ── PARCHE_JOINED_OR_CREATED ──────────────────────────────────────────────

    private List<String> handleParcheJoinedOrCreated(BadgeUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();

        // Any join or creation → Primer Parche
        tryAward(event.getUserId(), BADGE_PRIMER_PARCHE, awarded);

        if (Boolean.TRUE.equals(event.getIsCreator())) {
            // Anfitrión: created 2 parches as captain
            int totalCreated = event.getTotalParchesCreated() != null ? event.getTotalParchesCreated() : 0;
            if (totalCreated >= 2) {
                tryAward(event.getUserId(), BADGE_ANFITRION, awarded);
            }
            // Planificador: parche scheduled > 3 days ahead
            if (event.getParcheScheduledAt() != null) {
                long daysAhead = ChronoUnit.DAYS.between(LocalDateTime.now(), event.getParcheScheduledAt());
                if (daysAhead > 3) {
                    tryAward(event.getUserId(), BADGE_PLANIFICADOR, awarded);
                }
            }
        }
        return awarded;
    }

    // ── MEMBER_JOINED_PARCHE ──────────────────────────────────────────────────

    private List<String> handleMemberJoinedParche(BadgeUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();
        // The captain gets Imán Social when a new member joins their parche
        if (event.getCaptainUserId() != null) {
            tryAward(event.getCaptainUserId(), BADGE_IMAN_SOCIAL, awarded);
        }
        return awarded;
    }

    // ── ZONE_VISITED ──────────────────────────────────────────────────────────

    private List<String> handleZoneVisited(BadgeUnlockEventRequest event) {
        // RN-13.1.5: geo must be enabled; geo service sets this true on every published event
        if (!Boolean.TRUE.equals(event.getGeoLocationEnabled())) {
            log.debug("[CheckBadgeUnlock] ZONE_VISITED ignorado — geoLocationEnabled=false para userId={}", event.getUserId());
            return List.of();
        }

        String campusZone = event.getCampusZone();
        if (campusZone == null || campusZone.isBlank()) {
            log.debug("[CheckBadgeUnlock] ZONE_VISITED ignorado — campusZone vacío para userId={}", event.getUserId());
            return List.of();
        }

        String userId = event.getUserId();
        UserGamification user = userGamificationRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            log.debug("[CheckBadgeUnlock] ZONE_VISITED ignorado — usuario {} no encontrado en gamificación", userId);
            return List.of();
        }

        user.visitZone(campusZone);
        int totalZones = user.getVisitedCampusZones().size();

        // Update progress display for Explorador I and II (RN-13.1.3)
        badgeRepository.findByName(BADGE_EXPLORADOR_I)
                .ifPresent(b -> user.updateProgress(b.getId(), totalZones, 3));
        badgeRepository.findByName(BADGE_EXPLORADOR_II)
                .ifPresent(b -> user.updateProgress(b.getId(), totalZones, 5));

        userGamificationRepository.save(user);

        List<String> awarded = new ArrayList<>();
        if (totalZones >= 3) tryAward(userId, BADGE_EXPLORADOR_I, awarded);
        if (totalZones >= 5) tryAward(userId, BADGE_EXPLORADOR_II, awarded);
        return awarded;
    }

    // ── INSTITUTIONAL_EVENT_ATTENDED ─────────────────────────────────────────

    private List<String> handleEventAttended(BadgeUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();
        tryAward(event.getUserId(), BADGE_ASISTENTE, awarded);
        return awarded;
    }

    // ── FIRST_MESSAGE_SENT ────────────────────────────────────────────────────

    private List<String> handleFirstMessageSent(BadgeUnlockEventRequest event) {
        List<String> awarded = new ArrayList<>();
        tryAward(event.getUserId(), BADGE_PRIMER_MENSAJE, awarded);
        return awarded;
    }

    // ── COLECCIONISTA ─────────────────────────────────────────────────────────

    private void tryAwardColeccionista(String userId, List<String> awarded) {
        UserGamification user = userGamificationRepository.findByUserId(userId).orElse(null);
        if (user == null) return;

        List<String> allBadgeNames = List.of(
                BADGE_PRIMERA_CONEXION, BADGE_CONECTOR, BADGE_EMBAJADOR_SOCIAL,
                BADGE_PRIMER_PARCHE, BADGE_ANFITRION, BADGE_PLANIFICADOR,
                BADGE_EXPLORADOR_I, BADGE_EXPLORADOR_II, BADGE_ASISTENTE,
                BADGE_PRIMER_MENSAJE, BADGE_IMAN_SOCIAL, BADGE_METEORO_SOCIAL
        );

        boolean hasAll = allBadgeNames.stream().allMatch(name ->
                user.getEarnedBadges().stream().anyMatch(b -> b.getBadgeName().equals(name))
        );

        if (hasAll) {
            tryAward(userId, BADGE_COLECCIONISTA, awarded);
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void tryAward(String userId, String badgeName, List<String> awarded) {
        Optional<Badge> badge = badgeRepository.findByName(badgeName);
        if (badge.isEmpty()) {
            log.warn("[CheckBadgeUnlock] Badge '{}' not found in catalog", badgeName);
            return;
        }
        try {
            awardBadgeUseCase.execute(AwardBadgeRequest.builder()
                    .userId(userId)
                    .badgeId(badge.get().getId())
                    .build());
            awarded.add(badge.get().getId());
            log.info("[CheckBadgeUnlock] Badge '{}' awarded to userId={}", badgeName, userId);
        } catch (Exception e) {
            // Badge already earned or other non-fatal exception — skip silently
            log.debug("[CheckBadgeUnlock] Badge '{}' not awarded to userId={}: {}",
                    badgeName, userId, e.getMessage());
        }
    }
}
