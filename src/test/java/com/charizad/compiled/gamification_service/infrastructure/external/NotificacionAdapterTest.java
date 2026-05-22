package com.charizad.compiled.gamification_service.infrastructure.external;

import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
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

    private Mona Mona;

    @BeforeEach
    void setUp() {
        publisher = new RabbitNotificationPublisher(rabbitTemplate);
        ReflectionTestUtils.setField(publisher, "gamificationExchange", "gamification.events");
        ReflectionTestUtils.setField(publisher, "MonaEarnedKey", "Mona.earned");

        Mona = Mona.builder()
                .id("Mona-001")
                .name("Primer Parche")
                .category(MonaCategory.COMMON)
                .xpReward(100)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    @Test
    @DisplayName("notifyMonaEarned publica mensaje al exchange de RabbitMQ")
    void notifyMonaEarned_shouldPublishToRabbitMQ() {
        publisher.notifyMonaEarned("user-001", Mona);

        verify(rabbitTemplate).convertAndSend(
                eq("gamification.events"),
                eq("Mona.earned"),
                any(java.util.Map.class));
    }

    @Test
    @DisplayName("notifyMonaEarned no propaga excepción cuando RabbitMQ falla")
    void notifyMonaEarned_shouldSwallowException_whenRabbitFails() {
        doThrow(new RuntimeException("Broker unavailable"))
                .when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        // Should not throw
        publisher.notifyMonaEarned("user-001", Mona);
    }
}
