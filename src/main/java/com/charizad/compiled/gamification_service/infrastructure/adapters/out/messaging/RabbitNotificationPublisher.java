package com.charizad.compiled.gamification_service.infrastructure.adapters.out.messaging;

import com.charizad.compiled.gamification_service.domain.model.Mona;
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

    @Value("${rabbitmq.routing-key.Mona-earned:Mona.earned}")
    private String MonaEarnedKey;

    @Override
    @Async
    public void notifyMonaEarned(String userId, Mona Mona) {
        try {
            Map<String, Object> payload = Map.of(
                    "userId", userId,
                    "MonaId", Mona.getId(),
                    "MonaName", Mona.getName(),
                    "MonaCategory", Mona.getCategory().name(),
                    "xpAwarded", Mona.getXpReward(),
                    "occurredAt", LocalDateTime.now().toString()
            );
            rabbitTemplate.convertAndSend(gamificationExchange, MonaEarnedKey, payload);
            log.info("[RabbitMQ] Published Mona.earned → userId={} Mona={}", userId, Mona.getName());
        } catch (Exception e) {
            log.warn("[RabbitMQ] Could not publish Mona.earned for userId={}: {}", userId, e.getMessage());
        }
    }
}
