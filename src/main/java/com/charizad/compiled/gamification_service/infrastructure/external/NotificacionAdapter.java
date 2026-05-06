package com.charizad.compiled.gamification_service.infrastructure.external;

import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.ports.out.NotificationEventPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacionAdapter implements NotificationEventPort {

    private final RestTemplate restTemplate;

    @Value("${services.notification.url}")
    private String notificationServiceUrl;

    @Override
    @Async
    public void notifyBadgeEarned(String userId, Badge badge) {
        try {
            String url = notificationServiceUrl + "/api/v1/notifications/badge-earned";
            Map<String, Object> payload = Map.of(
                    "userId", userId,
                    "badgeId", badge.getId(),
                    "badgeName", badge.getName(),
                    "badgeCategory", badge.getCategory().name(),
                    "xpAwarded", badge.getXpReward()
            );
            restTemplate.postForEntity(url, payload, Void.class);
            log.info("Notificación enviada a M05 para usuario {} - insignia {}", userId, badge.getName());
        } catch (Exception e) {
            log.warn("No se pudo notificar al servicio de notificaciones (M05): {}", e.getMessage());
        }
    }
}
