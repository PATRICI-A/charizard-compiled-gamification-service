package com.charizad.compiled.gamification_service.infrastructure.external;

import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.infrastructure.adapters.out.messaging.RabbitNotificationPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionAdapterTest {

    @Mock private RabbitTemplate rabbitTemplate;

    private RabbitNotificationPublisher publisher;

    private Badge badge;

    @BeforeEach
    void setUp() {
        publisher = new RabbitNotificationPublisher(rabbitTemplate);
        ReflectionTestUtils.setField(publisher, "gamificationExchange", "gamification.events");
        ReflectionTestUtils.setField(publisher, "badgeEarnedKey", "badge.earned");

        badge = Badge.builder()
                .id("badge-001")
                .name("Primer Parche")
                .category(BadgeCategory.COMMON)
                .xpReward(100)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    @Test
    @DisplayName("notifyBadgeEarned publica mensaje al exchange de RabbitMQ")
    void notifyBadgeEarned_shouldPublishToRabbitMQ() {
        publisher.notifyBadgeEarned("user-001", badge);

        verify(rabbitTemplate).convertAndSend(
                eq("gamification.events"),
                eq("badge.earned"),
                any(java.util.Map.class));
    }

    @Test
    @DisplayName("notifyBadgeEarned no propaga excepción cuando RabbitMQ falla")
    void notifyBadgeEarned_shouldSwallowException_whenRabbitFails() {
        doThrow(new RuntimeException("Broker unavailable"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        // Should not throw
        publisher.notifyBadgeEarned("user-001", badge);
    }
}
