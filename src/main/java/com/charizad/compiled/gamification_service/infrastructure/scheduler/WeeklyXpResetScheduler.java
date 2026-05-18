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
public class WeeklyXpResetScheduler {

    private final UserGamificationRepositoryPort userGamificationRepository;

    /**
     * Reinicia el XP semanal y las monas semanales de todos los usuarios.
     * Se ejecuta todos los lunes a medianoche (00:00).
     * El reset de monas semanales (weeklyMonas) alimenta el ranking RF13.3.
     */
    @Scheduled(cron = "${scheduling.weekly-reset.cron}")
    public void resetWeeklyXp() {
        log.info("Iniciando reinicio semanal de XP y monas...");

        List<UserGamification> allUsers = userGamificationRepository.findAllOptedIn();
        allUsers.forEach(user -> {
            user.resetWeeklyXp();
            user.resetWeeklyMonas();
        });
        userGamificationRepository.saveAll(allUsers);

        log.info("Reinicio semanal completado para {} usuarios.", allUsers.size());
    }
}
