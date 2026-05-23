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
public class SemesterXpResetScheduler {

    private final UserGamificationRepositoryPort userGamificationRepository;

    /**
     * Reinicia el XP semestral y las monas semestrales de todos los usuarios.
     * Se ejecuta el 1 de febrero y el 1 de agosto a medianoche (00:00).
     */
    @Scheduled(cron = "0 0 0 1 2,8 *")
    public void resetSemesterStats() {
        log.info("Iniciando reinicio semestral de XP y monas...");

        List<UserGamification> allUsers = userGamificationRepository.findAllOptedIn();
        allUsers.forEach(UserGamification::resetSemesterStats);
        userGamificationRepository.saveAll(allUsers);

        log.info("Reinicio semestral completado para {} usuarios.", allUsers.size());
    }
}
