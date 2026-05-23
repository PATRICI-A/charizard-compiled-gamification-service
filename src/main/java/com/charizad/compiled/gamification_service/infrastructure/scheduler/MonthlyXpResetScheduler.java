package com.charizad.compiled.gamification_service.infrastructure.scheduler;

import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyXpResetScheduler {

    private final UserGamificationRepositoryPort userGamificationRepository;

    /**
     * Reinicia el XP mensual y las monas mensuales de todos los usuarios.
     * Se ejecuta el primer día de cada mes a medianoche (00:00).
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyStats() {
        log.info("Iniciando reinicio mensual de XP y monas...");

        List<UserGamification> allUsers = userGamificationRepository.findAllOptedIn();
        allUsers.forEach(UserGamification::resetMonthlyStats);
        userGamificationRepository.saveAll(allUsers);

        log.info("Reinicio mensual completado para {} usuarios.", allUsers.size());
    }
}
