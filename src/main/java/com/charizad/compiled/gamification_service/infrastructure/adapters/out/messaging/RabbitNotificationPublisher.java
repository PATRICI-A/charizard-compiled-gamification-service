package com.charizad.compiled.gamification_service.infrastructure.adapters.out.messaging;

import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.ports.out.NotificationEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Publica eventos de gamificación al exchange de RabbitMQ.
 * El servicio de notificaciones (M05) consume estos mensajes para
 * enviar notificaciones push/email al usuario.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitNotificationPublisher implements NotificationEventPort {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.gamification:gamification.events}")
    private String gamificationExchange;

    @Value("${rabbitmq.routing-key.badge-earned:badge.earned}")
    private String badgeEarnedKey;

    @Override
    @Async
    public void notifyBadgeEarned(String userId, Badge badge) {
        try {
            Map<String, Object> payload = Map.of(
                    "userId", userId,
                    "badgeId", badge.getId(),
                    "badgeName", badge.getName(),
                    "badgeCategory", badge.getCategory().name(),
                    "xpAwarded", badge.getXpReward(),
                    "occurredAt", LocalDateTime.now().toString()
            );
            rabbitTemplate.convertAndSend(gamificationExchange, badgeEarnedKey, payload);
            log.info("[RabbitMQ] Published badge.earned → userId={} badge={}", userId, badge.getName());
        } catch (Exception e) {
            log.warn("[RabbitMQ] Could not publish badge.earned for userId={}: {}", userId, e.getMessage());
        }
    }
}
