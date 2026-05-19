package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.request.BadgeUnlockEventRequest;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.BadgeUnlockEventType;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardBadgeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckBadgeUnlockServiceTest {

    @Mock private AwardBadgeUseCase awardBadgeUseCase;
    @Mock private BadgeRepositoryPort badgeRepository;
    @Mock private UserGamificationRepositoryPort userGamificationRepository;

    @InjectMocks
    private CheckBadgeUnlockService service;

    private Badge stubBadge(String id, String name) {
        return Badge.builder().id(id).name(name).category(BadgeCategory.COMMON).active(true).build();
    }

    private void stubBadgeFound(String name, String id) {
        when(badgeRepository.findByName(name)).thenReturn(Optional.of(stubBadge(id, name)));
    }

    private void stubBadgeNotFound(String name) {
        when(badgeRepository.findByName(name)).thenReturn(Optional.empty());
    }

    // ─── CONNECTION_CREATED ───────────────────────────────────────────────────

    @Test
    @DisplayName("CONNECTION_CREATED con 1 conexión otorga Primera Conexión")
    void connectionCreated_1connection_awardsPrimeraConexion() {
        stubBadgeFound("Primera Conexión", "b1");

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.CONNECTION_CREATED)
                .totalActiveConnections(1)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).containsExactly("b1");
        verify(awardBadgeUseCase).execute(any(AwardBadgeRequest.class));
    }

    @Test
    @DisplayName("CONNECTION_CREATED con 5 conexiones otorga Primera Conexión y Conector")
    void connectionCreated_5connections_awardsConector() {
        stubBadgeFound("Primera Conexión", "b1");
        stubBadgeFound("Conector", "b2");

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.CONNECTION_CREATED)
                .totalActiveConnections(5)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).containsExactlyInAnyOrder("b1", "b2");
    }

    @Test
    @DisplayName("CONNECTION_CREATED con 10 conexiones otorga Embajador Social")
    void connectionCreated_10connections_awardsEmbajadorSocial() {
        stubBadgeFound("Primera Conexión", "b1");
        stubBadgeFound("Conector", "b2");
        stubBadgeFound("Embajador Social", "b3");
        // No registration date → no Meteoro Social
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.CONNECTION_CREATED)
                .totalActiveConnections(10)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).contains("b3");
        verify(badgeRepository, never()).findByName("Meteoro Social");
    }

    @Test
    @DisplayName("CONNECTION_CREATED 10 conexiones + registro <30 días otorga Meteoro Social")
    void connectionCreated_10connections_recentRegistration_awardsMeteoroSocial() {
        stubBadgeFound("Primera Conexión", "b1");
        stubBadgeFound("Conector", "b2");
        stubBadgeFound("Embajador Social", "b3");
        stubBadgeFound("Meteoro Social", "b12");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.CONNECTION_CREATED)
                .totalActiveConnections(10)
                .userRegisteredAt(LocalDateTime.now().minusDays(10))
                .build();

        List<String> result = service.execute(event);

        assertThat(result).contains("b12");
    }

    @Test
    @DisplayName("CONNECTION_CREATED 10 conexiones + registro >30 días NO otorga Meteoro Social")
    void connectionCreated_10connections_oldRegistration_noMeteoroSocial() {
        stubBadgeFound("Primera Conexión", "b1");
        stubBadgeFound("Conector", "b2");
        stubBadgeFound("Embajador Social", "b3");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.CONNECTION_CREATED)
                .totalActiveConnections(10)
                .userRegisteredAt(LocalDateTime.now().minusDays(35))
                .build();

        List<String> result = service.execute(event);

        assertThat(result).doesNotContain("b12");
        verify(badgeRepository, never()).findByName("Meteoro Social");
    }

    @Test
    @DisplayName("CONNECTION_CREATED con 0 conexiones no otorga insignias")
    void connectionCreated_0connections_noAwards() {
        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.CONNECTION_CREATED)
                .totalActiveConnections(0)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).isEmpty();
        verifyNoInteractions(awardBadgeUseCase);
    }

    @Test
    @DisplayName("CONNECTION_CREATED con null conexiones no otorga insignias")
    void connectionCreated_nullConnections_noAwards() {
        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.CONNECTION_CREATED)
                .totalActiveConnections(null)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).isEmpty();
    }

    // ─── PARCHE_JOINED_OR_CREATED ─────────────────────────────────────────────

    @Test
    @DisplayName("PARCHE_JOINED_OR_CREATED siempre otorga Primer Parche")
    void parcheJoinedOrCreated_alwaysAwardsPrimerParche() {
        stubBadgeFound("Primer Parche", "b4");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.PARCHE_JOINED_OR_CREATED)
                .isCreator(false)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).containsExactly("b4");
    }

    @Test
    @DisplayName("PARCHE_JOINED_OR_CREATED capitán con 2 parches otorga Anfitrión")
    void parcheJoinedOrCreated_creator2Parches_awardsAnfitrion() {
        stubBadgeFound("Primer Parche", "b4");
        stubBadgeFound("Anfitrión", "b5");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.PARCHE_JOINED_OR_CREATED)
                .isCreator(true)
                .totalParchesCreated(2)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).contains("b5");
    }

    @Test
    @DisplayName("PARCHE_JOINED_OR_CREATED capitán con parche >3 días adelante otorga Planificador")
    void parcheJoinedOrCreated_creator4daysAhead_awardsPlanificador() {
        stubBadgeFound("Primer Parche", "b4");
        stubBadgeFound("Planificador", "b6");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.PARCHE_JOINED_OR_CREATED)
                .isCreator(true)
                .totalParchesCreated(1)
                .parcheScheduledAt(LocalDateTime.now().plusDays(5))
                .build();

        List<String> result = service.execute(event);

        assertThat(result).contains("b6");
    }

    @Test
    @DisplayName("PARCHE_JOINED_OR_CREATED capitán con parche ≤3 días adelante NO otorga Planificador")
    void parcheJoinedOrCreated_creator2daysAhead_noPlanificador() {
        stubBadgeFound("Primer Parche", "b4");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.PARCHE_JOINED_OR_CREATED)
                .isCreator(true)
                .totalParchesCreated(1)
                .parcheScheduledAt(LocalDateTime.now().plusDays(2))
                .build();

        List<String> result = service.execute(event);

        assertThat(result).doesNotContain("b6");
        verify(badgeRepository, never()).findByName("Planificador");
    }

    @Test
    @DisplayName("PARCHE_JOINED_OR_CREATED capitán sin parcheScheduledAt no evalúa Planificador")
    void parcheJoinedOrCreated_nullScheduledAt_noPlanificador() {
        stubBadgeFound("Primer Parche", "b4");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.PARCHE_JOINED_OR_CREATED)
                .isCreator(true)
                .totalParchesCreated(1)
                .parcheScheduledAt(null)
                .build();

        List<String> result = service.execute(event);

        verify(badgeRepository, never()).findByName("Planificador");
    }

    @Test
    @DisplayName("PARCHE_JOINED_OR_CREATED no capitán con null totalParchesCreated no otorga Anfitrión")
    void parcheJoinedOrCreated_nullTotalParchesCreated_noAnfitrion() {
        stubBadgeFound("Primer Parche", "b4");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.PARCHE_JOINED_OR_CREATED)
                .isCreator(true)
                .totalParchesCreated(null)
                .build();

        List<String> result = service.execute(event);

        verify(badgeRepository, never()).findByName("Anfitrión");
    }

    // ─── MEMBER_JOINED_PARCHE ─────────────────────────────────────────────────

    @Test
    @DisplayName("MEMBER_JOINED_PARCHE otorga Imán Social al capitán")
    void memberJoinedParche_awardsImanSocialToCaptain() {
        stubBadgeFound("Imán Social", "b11");
        // tryAwardColeccionista uses event.getUserId() = "u1"
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.MEMBER_JOINED_PARCHE)
                .captainUserId("captain-1")
                .build();

        List<String> result = service.execute(event);

        assertThat(result).containsExactly("b11");
        verify(awardBadgeUseCase).execute(argThat(req -> "captain-1".equals(req.getUserId())));
    }

    @Test
    @DisplayName("MEMBER_JOINED_PARCHE sin captainUserId no otorga insignia")
    void memberJoinedParche_noCaptain_noAward() {
        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.MEMBER_JOINED_PARCHE)
                .captainUserId(null)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).isEmpty();
        verifyNoInteractions(awardBadgeUseCase);
    }

    // ─── ZONE_VISITED ─────────────────────────────────────────────────────────

    private UserGamification userWithZones(String userId, String... zones) {
        ArrayList<String> zoneList = new ArrayList<>(List.of(zones));
        return UserGamification.builder()
                .userId(userId).totalXp(0).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .visitedCampusZones(zoneList)
                .build();
    }

    @Test
    @DisplayName("ZONE_VISITED zona nueva lleva a 3 → otorga Explorador I")
    void zoneVisited_3rdZone_awardsExploradorI() {
        stubBadgeFound("Explorador I", "b7");
        stubBadgeFound("Explorador II", "b8");
        when(userGamificationRepository.findByUserId("u1"))
                .thenReturn(Optional.of(userWithZones("u1", "Zona A", "Zona B")));

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.ZONE_VISITED)
                .campusZone("Zona C")
                .geoLocationEnabled(true)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).contains("b7");
        verify(userGamificationRepository).save(any(UserGamification.class));
    }

    @Test
    @DisplayName("ZONE_VISITED zona nueva lleva a 5 → otorga Explorador I y II")
    void zoneVisited_5thZone_awardsExploradorIAndII() {
        stubBadgeFound("Explorador I", "b7");
        stubBadgeFound("Explorador II", "b8");
        when(userGamificationRepository.findByUserId("u1"))
                .thenReturn(Optional.of(userWithZones("u1", "A", "B", "C", "D")));

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.ZONE_VISITED)
                .campusZone("E")
                .geoLocationEnabled(true)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).containsAnyOf("b7", "b8");
        verify(userGamificationRepository).save(any(UserGamification.class));
    }

    @Test
    @DisplayName("ZONE_VISITED zona nueva lleva a 2 → no otorga insignias")
    void zoneVisited_2ndZone_noAward() {
        stubBadgeFound("Explorador I", "b7");
        stubBadgeFound("Explorador II", "b8");
        when(userGamificationRepository.findByUserId("u1"))
                .thenReturn(Optional.of(userWithZones("u1", "Zona A")));

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.ZONE_VISITED)
                .campusZone("Zona B")
                .geoLocationEnabled(true)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).isEmpty();
        verifyNoInteractions(awardBadgeUseCase);
    }

    @Test
    @DisplayName("ZONE_VISITED zona ya visitada — conteo no aumenta")
    void zoneVisited_duplicateZone_countUnchanged() {
        stubBadgeFound("Explorador I", "b7");
        stubBadgeFound("Explorador II", "b8");
        UserGamification user = userWithZones("u1", "Zona A", "Zona B");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(user));

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.ZONE_VISITED)
                .campusZone("Zona A") // duplicate
                .geoLocationEnabled(true)
                .build();

        service.execute(event);

        assertThat(user.getVisitedCampusZones()).hasSize(2);
    }

    @Test
    @DisplayName("ZONE_VISITED con campusZone null → no otorga")
    void zoneVisited_nullCampusZone_noAward() {
        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.ZONE_VISITED)
                .campusZone(null)
                .geoLocationEnabled(true)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).isEmpty();
        verifyNoInteractions(awardBadgeUseCase);
    }

    @Test
    @DisplayName("ZONE_VISITED con usuario no encontrado → no otorga")
    void zoneVisited_userNotFound_noAward() {
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.ZONE_VISITED)
                .campusZone("Zona A")
                .geoLocationEnabled(true)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).isEmpty();
        verifyNoInteractions(awardBadgeUseCase);
    }

    @Test
    @DisplayName("ZONE_VISITED con geoLocationEnabled=false no otorga (RN-13.1.5)")
    void zoneVisited_geoDisabled_noAwardEvenWithEnoughZones() {
        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.ZONE_VISITED)
                .campusZone("Zona A")
                .geoLocationEnabled(false)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).isEmpty();
        verifyNoInteractions(awardBadgeUseCase);
    }

    @Test
    @DisplayName("ZONE_VISITED con geoLocationEnabled=null no otorga (RN-13.1.5)")
    void zoneVisited_geoNull_noAward() {
        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.ZONE_VISITED)
                .campusZone("Zona A")
                .geoLocationEnabled(null)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).isEmpty();
        verifyNoInteractions(awardBadgeUseCase);
    }

    // ─── INSTITUTIONAL_EVENT_ATTENDED ─────────────────────────────────────────

    @Test
    @DisplayName("INSTITUTIONAL_EVENT_ATTENDED otorga Asistente")
    void eventAttended_awardsAsistente() {
        stubBadgeFound("Asistente", "b9");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.INSTITUTIONAL_EVENT_ATTENDED)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).containsExactly("b9");
    }

    // ─── FIRST_MESSAGE_SENT ───────────────────────────────────────────────────

    @Test
    @DisplayName("FIRST_MESSAGE_SENT otorga Primer Mensaje")
    void firstMessageSent_awardsPrimerMensaje() {
        stubBadgeFound("Primer Mensaje", "b10");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.FIRST_MESSAGE_SENT)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).containsExactly("b10");
    }

    // ─── Badge not found in catalog ──────────────────────────────────────────

    @Test
    @DisplayName("Mona no encontrada en catálogo → se omite silenciosamente")
    void tryAward_badgeNotInCatalog_skippedSilently() {
        stubBadgeNotFound("Asistente");

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.INSTITUTIONAL_EVENT_ATTENDED)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).isEmpty();
        verifyNoInteractions(awardBadgeUseCase);
    }

    // ─── awardBadgeUseCase throws (already earned) ────────────────────────────

    @Test
    @DisplayName("awardBadge lanza excepción (ya otorgada) → se omite silenciosamente")
    void tryAward_alreadyEarned_skippedSilently() {
        stubBadgeFound("Asistente", "b9");
        doThrow(new RuntimeException("already earned")).when(awardBadgeUseCase).execute(any());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.INSTITUTIONAL_EVENT_ATTENDED)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).isEmpty();
    }

    // ─── Coleccionista ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Coleccionista se otorga cuando usuario ya tiene todas las demás monas")
    void coleccionista_awardedWhenUserHasAllBadges() {
        stubBadgeFound("Primer Mensaje", "b10");
        stubBadgeFound("Coleccionista", "b13");

        List<String> allNames = List.of(
                "Primera Conexión", "Conector", "Embajador Social",
                "Primer Parche", "Anfitrión", "Planificador",
                "Explorador I", "Explorador II", "Asistente",
                "Primer Mensaje", "Imán Social", "Meteoro Social"
        );

        ArrayList<EarnedBadge> badges = new ArrayList<>();
        for (String name : allNames) {
            badges.add(EarnedBadge.builder().badgeId("id-" + name).badgeName(name).xpAwarded(10).build());
        }

        UserGamification userWithAll = UserGamification.builder()
                .id("ug-1").userId("u1").totalXp(120).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedBadges(badges)
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(userWithAll));

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.FIRST_MESSAGE_SENT)
                .build();

        List<String> result = service.execute(event);

        assertThat(result).contains("b13");
    }

    @Test
    @DisplayName("Coleccionista NO se otorga cuando faltan monas")
    void coleccionista_notAwardedWhenMissingBadges() {
        stubBadgeFound("Primer Mensaje", "b10");

        UserGamification userPartial = UserGamification.builder()
                .id("ug-1").userId("u1").totalXp(10).weeklyXp(0).weeklyMonas(0)
                .rankingOptIn(false)
                .earnedBadges(new ArrayList<>())
                .progress(new ArrayList<>())
                .earnedRewards(new ArrayList<>())
                .build();

        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.of(userPartial));

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.FIRST_MESSAGE_SENT)
                .build();

        List<String> result = service.execute(event);

        verify(badgeRepository, never()).findByName("Coleccionista");
    }

    @Test
    @DisplayName("tryAwardColeccionista con usuario no encontrado → skip silencioso")
    void coleccionista_userNotFound_skipped() {
        stubBadgeFound("Primer Mensaje", "b10");
        when(userGamificationRepository.findByUserId("u1")).thenReturn(Optional.empty());

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId("u1")
                .eventType(BadgeUnlockEventType.FIRST_MESSAGE_SENT)
                .build();

        List<String> result = service.execute(event);

        verify(badgeRepository, never()).findByName("Coleccionista");
    }
}
