package com.charizad.compiled.gamification_service.domain.ports.out;

import com.charizad.compiled.gamification_service.domain.model.Mona;

public interface NotificationEventPort {
    /**
     * Notifica al servicio de notificaciones (M05) que un usuario desbloqueó una insignia.
     */
    void notifyMonaEarned(String userId, Mona Mona);
}
