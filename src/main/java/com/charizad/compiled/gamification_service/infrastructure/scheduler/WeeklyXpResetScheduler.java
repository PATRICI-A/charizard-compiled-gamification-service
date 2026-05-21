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

    /** Resets weekly XP and weekly monas for all opted-in users. Runs every Monday at 00:00 (RN-13.3.3). */
    @Scheduled(cron = "${scheduling.weekly-reset.cron}")
    public void resetWeeklyXp() {
        log.info("Starting weekly XP and monas reset...");
        List<UserGamification> users = userGamificationRepository.findAllOptedIn();
        users.forEach(user -> {
            user.resetWeeklyXp();
            user.resetWeeklyMonas();
        });
        userGamificationRepository.saveAll(users);
        log.info("Weekly reset completed for {} users.", users.size());
    }

    /** Resets monthly monas for all opted-in users. Runs on the first day of each month at 00:00 (RN-13.3.3). */
    @Scheduled(cron = "${scheduling.monthly-reset.cron}")
    public void resetMonthlyMonas() {
        log.info("Starting monthly monas reset...");
        List<UserGamification> users = userGamificationRepository.findAllOptedIn();
        users.forEach(UserGamification::resetMonthlyMonas);
        userGamificationRepository.saveAll(users);
        log.info("Monthly reset completed for {} users.", users.size());
    }

    /** Resets semester monas for all opted-in users. Runs at the start of each academic semester (RN-13.3.3). */
    @Scheduled(cron = "${scheduling.semestral-reset.cron}")
    public void resetSemestralMonas() {
        log.info("Starting semester monas reset...");
        List<UserGamification> users = userGamificationRepository.findAllOptedIn();
        users.forEach(UserGamification::resetSemestralMonas);
        userGamificationRepository.saveAll(users);
        log.info("Semester reset completed for {} users.", users.size());
    }
}
