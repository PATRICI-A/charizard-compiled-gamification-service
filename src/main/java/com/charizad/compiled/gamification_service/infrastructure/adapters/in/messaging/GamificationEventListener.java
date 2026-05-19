package com.charizad.compiled.gamification_service.infrastructure.adapters.in.messaging;

import com.charizad.compiled.gamification_service.application.dto.request.BadgeUnlockEventRequest;
import com.charizad.compiled.gamification_service.domain.model.BadgeUnlockEventType;
import com.charizad.compiled.gamification_service.domain.ports.in.CheckBadgeUnlockUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Consumes events published by other microservices and triggers badge unlock evaluation.
 *
 * Each method maps a RabbitMQ event to a {@link BadgeUnlockEventRequest} and delegates
 * to {@link CheckBadgeUnlockUseCase}.
 *
 * Expected payload format for all events: Map<String, Object> with at least "userId".
 * Additional fields depend on the event type — see each method.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GamificationEventListener {

    private final CheckBadgeUnlockUseCase checkBadgeUnlockUseCase;

    /**
     * Published by social-matching when a user creates or reaches a connection milestone.
     * Expected payload: { userId, totalActiveConnections, userRegisteredAt (ISO-8601) }
     * Evaluates: Primera Conexión, Conector, Embajador Social, Meteoro Social.
     */
    @RabbitListener(queues = "${rabbitmq.queue.connection-created:gamification.connection.queue}")
    public void onConnectionCreated(Map<String, Object> payload) {
        log.info("[RabbitMQ] connection.created received for userId={}", payload.get("userId"));
        try {
            BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                    .userId((String) payload.get("userId"))
                    .eventType(BadgeUnlockEventType.CONNECTION_CREATED)
                    .totalActiveConnections(toInt(payload.get("totalActiveConnections")))
                    .userRegisteredAt(toLocalDateTime(payload.get("userRegisteredAt")))
                    .build();
            checkBadgeUnlockUseCase.execute(event);
        } catch (Exception e) {
            log.error("[RabbitMQ] Error processing connection.created: {}", e.getMessage(), e);
        }
    }

    /**
     * Published by hangout-service when a user joins or creates a parche.
     * Expected payload: { userId, isCreator, totalParchesCreated, parcheScheduledAt (ISO-8601) }
     * Evaluates: Primer Parche, Anfitrión, Planificador.
     */
    @RabbitListener(queues = "${rabbitmq.queue.parche-created:gamification.parche.queue}")
    public void onParcheCreated(Map<String, Object> payload) {
        log.info("[RabbitMQ] parche.created received for userId={}", payload.get("userId"));
        try {
            BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                    .userId((String) payload.get("userId"))
                    .eventType(BadgeUnlockEventType.PARCHE_JOINED_OR_CREATED)
                    .isCreator((Boolean) payload.getOrDefault("isCreator", false))
                    .totalParchesCreated(toInt(payload.get("totalParchesCreated")))
                    .parcheScheduledAt(toLocalDateTime(payload.get("parcheScheduledAt")))
                    .build();
            checkBadgeUnlockUseCase.execute(event);
        } catch (Exception e) {
            log.error("[RabbitMQ] Error processing parche.created: {}", e.getMessage(), e);
        }
    }

    /**
     * Published by hangout-service when a new member joins a parche.
     * Expected payload: { userId (new member), captainUserId }
     * Evaluates: Imán Social (for the captain).
     */
    @RabbitListener(queues = "${rabbitmq.queue.member-joined:gamification.member.queue}")
    public void onMemberJoined(Map<String, Object> payload) {
        log.info("[RabbitMQ] member.joined received for userId={}", payload.get("userId"));
        try {
            BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                    .userId((String) payload.get("userId"))
                    .eventType(BadgeUnlockEventType.MEMBER_JOINED_PARCHE)
                    .captainUserId((String) payload.get("captainUserId"))
                    .build();
            checkBadgeUnlockUseCase.execute(event);
        } catch (Exception e) {
            log.error("[RabbitMQ] Error processing member.joined: {}", e.getMessage(), e);
        }
    }

    /**
     * Published by hangout-service when a user sends their first message in a parche.
     * Expected payload: { userId }
     * Evaluates: Primer Mensaje.
     */
    @RabbitListener(queues = "${rabbitmq.queue.message-sent:gamification.message.queue}")
    public void onMessageSent(Map<String, Object> payload) {
        log.info("[RabbitMQ] message.sent received for userId={}", payload.get("userId"));
        try {
            BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                    .userId((String) payload.get("userId"))
                    .eventType(BadgeUnlockEventType.FIRST_MESSAGE_SENT)
                    .build();
            checkBadgeUnlockUseCase.execute(event);
        } catch (Exception e) {
            log.error("[RabbitMQ] Error processing message.sent: {}", e.getMessage(), e);
        }
    }

    /**
     * Published by geo service on geo.exchange / geo.location.updated when a user updates location.
     * Expected payload: { userId, latitude, longitude, campusZone, updatedAt }
     * Evaluates: Explorador I (3 distinct zones), Explorador II (5 distinct zones).
     * geoLocationEnabled hardcoded true — geo service only publishes after successful location validation.
     */
    @RabbitListener(queues = "${rabbitmq.queue.zone-visited:gamification.zone.queue}")
    public void onZoneVisited(Map<String, Object> payload) {
        log.info("[RabbitMQ] geo.location.updated received for userId={}", payload.get("userId"));
        try {
            BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                    .userId((String) payload.get("userId"))
                    .eventType(BadgeUnlockEventType.ZONE_VISITED)
                    .campusZone((String) payload.get("campusZone"))
                    .geoLocationEnabled(true)
                    .build();
            checkBadgeUnlockUseCase.execute(event);
        } catch (Exception e) {
            log.error("[RabbitMQ] Error processing geo.location.updated: {}", e.getMessage(), e);
        }
    }

    /**
     * Published by institutional events service when a user attends an event.
     * Expected payload: { userId }
     * Evaluates: Asistente.
     */
    @RabbitListener(queues = "${rabbitmq.queue.event-attended:gamification.institutional.queue}")
    public void onEventAttended(Map<String, Object> payload) {
        log.info("[RabbitMQ] event.attended received for userId={}", payload.get("userId"));
        try {
            BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                    .userId((String) payload.get("userId"))
                    .eventType(BadgeUnlockEventType.INSTITUTIONAL_EVENT_ATTENDED)
                    .build();
            checkBadgeUnlockUseCase.execute(event);
        } catch (Exception e) {
            log.error("[RabbitMQ] Error processing event.attended: {}", e.getMessage(), e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        return Integer.parseInt(value.toString());
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDateTime ldt) return ldt;
        return LocalDateTime.parse(value.toString());
    }
}
