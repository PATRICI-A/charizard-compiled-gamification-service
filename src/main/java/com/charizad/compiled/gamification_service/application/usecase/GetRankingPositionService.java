package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.domain.model.NivelCalculator;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingPositionUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class GetRankingPositionService implements GetRankingPositionUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public RankingPositionResponse execute(String userId, RankingType type) {
        UserGamification user = userGamificationRepository.findByUserId(userId)
                .orElse(null);

        LocalDate now = LocalDate.now();
        LocalDate periodStart = calculatePeriodStart(now, type);
        LocalDate periodEnd = calculatePeriodEnd(now, type);

        if (user == null || !user.isRankingOptIn()) {
            return RankingPositionResponse.builder()
                    .userId(userId)
                    .position(null)
                    .monasThisPeriod(0)
                    .rankingOptIn(user != null && user.isRankingOptIn())
                    .periodStart(periodStart)
                    .periodEnd(periodEnd)
                    .rankingType(type)
                    .levelName(NivelCalculator.getNivelName(1))
                    .build();
        }

        int monasThisPeriod = switch (type) {
            case WEEKLY -> user.getWeeklyMonas();
            case MONTHLY -> user.getMonthlyMonas();
            case SEMESTER -> user.getSemesterMonas();
        };

        long usersAhead = switch (type) {
            case WEEKLY -> userGamificationRepository.countOptedInWithMoreWeeklyMonasThan(monasThisPeriod);
            case MONTHLY -> userGamificationRepository.countOptedInWithMoreMonthlyMonasThan(monasThisPeriod);
            case SEMESTER -> userGamificationRepository.countOptedInWithMoreSemesterMonasThan(monasThisPeriod);
        };
        
        int position = (int) usersAhead + 1;

        int totalXp = user.getTotalXp();
        int nivel = NivelCalculator.getNivel(totalXp);

        return RankingPositionResponse.builder()
                .userId(userId)
                .position(position)
                .monasThisPeriod(monasThisPeriod)
                .rankingOptIn(true)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .rankingType(type)
                .levelName(NivelCalculator.getNivelName(nivel))
                .build();
    }

    private LocalDate calculatePeriodStart(LocalDate now, RankingType type) {
        return switch (type) {
            case WEEKLY -> now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case MONTHLY -> now.with(TemporalAdjusters.firstDayOfMonth());
            case SEMESTER -> now.getMonthValue() < 7 ? 
                    LocalDate.of(now.getYear(), 2, 1) : 
                    LocalDate.of(now.getYear(), 8, 1);
        };
    }

    private LocalDate calculatePeriodEnd(LocalDate now, RankingType type) {
        return switch (type) {
            case WEEKLY -> now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            case MONTHLY -> now.with(TemporalAdjusters.lastDayOfMonth());
            case SEMESTER -> now.getMonthValue() < 7 ? 
                    LocalDate.of(now.getYear(), 6, 30) : 
                    LocalDate.of(now.getYear(), 12, 31);
        };
    }
}
