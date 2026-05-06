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
     * Reinicia el XP semanal de todos los usuarios participantes del ranking.
     * Se ejecuta todos los lunes a medianoche (00:00).
     */
    @Scheduled(cron = "${scheduling.weekly-reset.cron}")
    public void resetWeeklyXp() {
        log.info("Iniciando reinicio semanal de XP...");

        List<UserGamification> allUsers = userGamificationRepository.findAllOptedIn();
        allUsers.forEach(UserGamification::resetWeeklyXp);
        userGamificationRepository.saveAll(allUsers);

        log.info("Reinicio semanal completado para {} usuarios.", allUsers.size());
    }
}
