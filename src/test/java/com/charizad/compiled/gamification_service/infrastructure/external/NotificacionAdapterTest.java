package com.charizad.compiled.gamification_service.infrastructure.external;

import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionAdapterTest {

    @Mock private RestTemplate restTemplate;

    private NotificacionAdapter adapter;

    private Badge badge;

    @BeforeEach
    void setUp() {
        adapter = new NotificacionAdapter(restTemplate);
        ReflectionTestUtils.setField(adapter, "notificationServiceUrl", "http://notification-service");
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
    @DisplayName("notifyBadgeEarned envía POST al servicio de notificaciones")
    void notifyBadgeEarned_shouldSendPostToNotificationService() {
        adapter.notifyBadgeEarned("user-001", badge);

        verify(restTemplate).postForEntity(
                eq("http://notification-service/api/v1/notifications/badge-earned"),
                any(),
                eq(Void.class));
    }

    @Test
    @DisplayName("notifyBadgeEarned no propaga excepción cuando el servicio falla")
    void notifyBadgeEarned_shouldSwallowException_whenServiceFails() {
        doThrow(new RuntimeException("Service unavailable"))
                .when(restTemplate).postForEntity(anyString(), any(), eq(Void.class));

        adapter.notifyBadgeEarned("user-001", badge);
    }
}
