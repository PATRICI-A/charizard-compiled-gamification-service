package com.charizad.compiled.gamification_service.domain.ports.out;

import com.charizad.compiled.gamification_service.domain.model.Badge;

public interface NotificationEventPort {
    /**
     * Notifica al servicio de notificaciones (M05) que un usuario desbloqueó una insignia.
     */
    void notifyBadgeEarned(String userId, Badge badge);
}
