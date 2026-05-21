package com.charizad.compiled.gamification_service.domain.ports.out;

import com.charizad.compiled.gamification_service.domain.model.Badge;

public interface NotificationEventPort {
    /**
     * Notifica al servicio de notificaciones (M05) que un usuario desbloqueó una insignia.
     */
    void notifyBadgeEarned(String userId, Badge badge);

    /**
     * Notifica al servicio de notificaciones (M05) que un usuario desbloqueó una nueva mona.
     * Publica a gamification.events con routing key achievement.unlocked.
     */
    void notifyAchievementUnlocked(String userId, Badge badge);
}
