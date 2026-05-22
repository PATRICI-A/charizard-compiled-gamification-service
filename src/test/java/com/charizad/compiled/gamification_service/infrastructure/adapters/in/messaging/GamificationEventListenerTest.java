package com.charizad.compiled.gamification_service.infrastructure.adapters.in.messaging;

import com.charizad.compiled.gamification_service.domain.model.MonaUnlockEventType;
import com.charizad.compiled.gamification_service.domain.ports.in.CheckMonaUnlockUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationEventListenerTest {

    @Mock private CheckMonaUnlockUseCase checkMonaUnlockUseCase;

    @InjectMocks
    private GamificationEventListener listener;

    // ── onConnectionCreated ───────────────────────────────────────────────────

    @Test
    @DisplayName("onConnectionCreated — mapea userId y totalActiveConnections")
    void onConnectionCreated_mapsPayloadCorrectly() {
        Map<String, Object> payload = Map.of(
                "userId", "u1",
                "totalActiveConnections", 5,
                "userRegisteredAt", "2026-01-01T00:00:00"
        );
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onConnectionCreated(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e ->
                "u1".equals(e.getUserId()) &&
                e.getEventType() == MonaUnlockEventType.CONNECTION_CREATED &&
                e.getTotalActiveConnections() == 5 &&
                e.getUserRegisteredAt() != null
        ));
    }

    @Test
    @DisplayName("onConnectionCreated — userRegisteredAt nulo no explota")
    void onConnectionCreated_nullRegisteredAt_noException() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", "u1");
        payload.put("totalActiveConnections", 3);
        payload.put("userRegisteredAt", null);

        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onConnectionCreated(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e -> e.getUserRegisteredAt() == null));
    }

    @Test
    @DisplayName("onConnectionCreated — userRegisteredAt como LocalDateTime directo")
    void onConnectionCreated_registeredAtAsLocalDateTime() {
        LocalDateTime dt = LocalDateTime.of(2026, 1, 1, 0, 0);
        Map<String, Object> payload = Map.of(
                "userId", "u2",
                "totalActiveConnections", 1,
                "userRegisteredAt", dt
        );
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onConnectionCreated(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e -> dt.equals(e.getUserRegisteredAt())));
    }

    @Test
    @DisplayName("onConnectionCreated — excepción en use case no propaga")
    void onConnectionCreated_useCaseThrows_noException() {
        Map<String, Object> payload = Map.of("userId", "u1", "totalActiveConnections", 1);
        when(checkMonaUnlockUseCase.execute(any())).thenThrow(new RuntimeException("fail"));

        // Should not throw
        listener.onConnectionCreated(payload);
    }

    // ── onParcheCreated ───────────────────────────────────────────────────────

    @Test
    @DisplayName("onParcheCreated — isCreator=true, totalParchesCreated, parcheScheduledAt mapeados")
    void onParcheCreated_mapsCreatorPayload() {
        LocalDateTime scheduled = LocalDateTime.now().plusDays(5);
        Map<String, Object> payload = Map.of(
                "userId", "u1",
                "isCreator", true,
                "totalParchesCreated", 3,
                "parcheScheduledAt", scheduled.toString()
        );
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onParcheCreated(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e ->
                "u1".equals(e.getUserId()) &&
                e.getEventType() == MonaUnlockEventType.PARCHE_JOINED_OR_CREATED &&
                Boolean.TRUE.equals(e.getIsCreator()) &&
                e.getTotalParchesCreated() == 3
        ));
    }

    @Test
    @DisplayName("onParcheCreated — isCreator ausente default false")
    void onParcheCreated_isCreatorAbsent_defaultsFalse() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", "u1");
        payload.put("totalParchesCreated", 1);
        payload.put("parcheScheduledAt", null);
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onParcheCreated(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e -> Boolean.FALSE.equals(e.getIsCreator())));
    }

    @Test
    @DisplayName("onParcheCreated — excepción en use case no propaga")
    void onParcheCreated_useCaseThrows_noException() {
        Map<String, Object> payload = Map.of("userId", "u1");
        when(checkMonaUnlockUseCase.execute(any())).thenThrow(new RuntimeException("fail"));

        listener.onParcheCreated(payload);
    }

    // ── onMemberJoined ────────────────────────────────────────────────────────

    @Test
    @DisplayName("onMemberJoined — userId y captainUserId mapeados")
    void onMemberJoined_mapsPayload() {
        Map<String, Object> payload = Map.of("userId", "u1", "captainUserId", "captain1");
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onMemberJoined(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e ->
                "u1".equals(e.getUserId()) &&
                e.getEventType() == MonaUnlockEventType.MEMBER_JOINED_PARCHE &&
                "captain1".equals(e.getCaptainUserId())
        ));
    }

    @Test
    @DisplayName("onMemberJoined — excepción en use case no propaga")
    void onMemberJoined_useCaseThrows_noException() {
        Map<String, Object> payload = Map.of("userId", "u1", "captainUserId", "c1");
        when(checkMonaUnlockUseCase.execute(any())).thenThrow(new RuntimeException("fail"));

        listener.onMemberJoined(payload);
    }

    // ── onMessageSent ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("onMessageSent — eventType=FIRST_MESSAGE_SENT")
    void onMessageSent_correctEventType() {
        Map<String, Object> payload = Map.of("userId", "u1");
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onMessageSent(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e ->
                "u1".equals(e.getUserId()) &&
                e.getEventType() == MonaUnlockEventType.FIRST_MESSAGE_SENT
        ));
    }

    @Test
    @DisplayName("onMessageSent — excepción no propaga")
    void onMessageSent_useCaseThrows_noException() {
        Map<String, Object> payload = Map.of("userId", "u1");
        when(checkMonaUnlockUseCase.execute(any())).thenThrow(new RuntimeException("fail"));

        listener.onMessageSent(payload);
    }

    // ── onZoneVisited ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("onZoneVisited — campusZone mapeado desde payload geo, geoLocationEnabled=true")
    void onZoneVisited_mapsCampusZone() {
        Map<String, Object> payload = Map.of(
                "userId", "u1",
                "latitude", 4.628,
                "longitude", -74.064,
                "campusZone", "Bloque de Ingeniería",
                "updatedAt", "2026-05-19T10:00:00"
        );
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onZoneVisited(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e ->
                "u1".equals(e.getUserId()) &&
                e.getEventType() == MonaUnlockEventType.ZONE_VISITED &&
                "Bloque de Ingeniería".equals(e.getCampusZone()) &&
                Boolean.TRUE.equals(e.getGeoLocationEnabled())
        ));
    }

    @Test
    @DisplayName("onZoneVisited — campusZone null en payload → se pasa null")
    void onZoneVisited_nullCampusZone_passedAsNull() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", "u1");
        payload.put("campusZone", null);
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onZoneVisited(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e -> e.getCampusZone() == null));
    }

    @Test
    @DisplayName("onZoneVisited — geoLocationEnabled siempre true independiente del payload")
    void onZoneVisited_geoLocationEnabledAlwaysTrue() {
        Map<String, Object> payload = Map.of("userId", "u1", "campusZone", "Cafetería");
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onZoneVisited(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e -> Boolean.TRUE.equals(e.getGeoLocationEnabled())));
    }

    @Test
    @DisplayName("onZoneVisited — excepción no propaga")
    void onZoneVisited_useCaseThrows_noException() {
        Map<String, Object> payload = Map.of("userId", "u1", "campusZone", "Zona A");
        when(checkMonaUnlockUseCase.execute(any())).thenThrow(new RuntimeException("fail"));

        listener.onZoneVisited(payload);
    }

    // ── onEventAttended ───────────────────────────────────────────────────────

    @Test
    @DisplayName("onEventAttended — eventType=INSTITUTIONAL_EVENT_ATTENDED")
    void onEventAttended_correctEventType() {
        Map<String, Object> payload = Map.of("userId", "u1");
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onEventAttended(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e ->
                "u1".equals(e.getUserId()) &&
                e.getEventType() == MonaUnlockEventType.INSTITUTIONAL_EVENT_ATTENDED
        ));
    }

    @Test
    @DisplayName("onEventAttended — excepción no propaga")
    void onEventAttended_useCaseThrows_noException() {
        Map<String, Object> payload = Map.of("userId", "u1");
        when(checkMonaUnlockUseCase.execute(any())).thenThrow(new RuntimeException("fail"));

        listener.onEventAttended(payload);
    }

    // ── toInt helper paths ────────────────────────────────────────────────────

    @Test
    @DisplayName("toInt — value null → 0")
    void toInt_null_returnsZero() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", "u1");
        payload.put("totalActiveConnections", null);
        when(checkMonaUnlockUseCase.execute(any())).thenReturn(List.of());

        listener.onConnectionCreated(payload);

        verify(checkMonaUnlockUseCase).execute(argThat(e -> e.getTotalActiveConnections() == 0));
    }
}
