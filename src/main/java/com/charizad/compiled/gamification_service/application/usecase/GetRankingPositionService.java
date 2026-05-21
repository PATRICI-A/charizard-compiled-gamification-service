package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingPositionUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRankingPositionService implements GetRankingPositionUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public RankingPositionResponse execute(String userId, RankingType type) {
        LocalDate periodStart = getPeriodStart(type);
        LocalDate periodEnd   = getPeriodEnd(type);

        UserGamification user = userGamificationRepository.findByUserId(userId).orElse(null);

        if (user == null || !user.isRankingOptIn()) {
            return RankingPositionResponse.builder()
                    .position(null)
                    .monasThisPeriod(0)
                    .rankingOptIn(false)
                    .periodStart(periodStart)
                    .periodEnd(periodEnd)
                    .rankingType(type)
                    .build();
        }

        List<UserGamification> ranked = userGamificationRepository.findAllOptedInRankedFor(type);

        int position = -1;
        for (int i = 0; i < ranked.size(); i++) {
            if (ranked.get(i).getUserId().equals(userId)) {
                position = i + 1;
                break;
            }
        }

        int periodMonas = switch (type) {
            case MONTHLY -> user.getMonthlyMonas();
            case SEMESTER -> user.getSemestralMonas();
            default -> user.getWeeklyMonas();
        };

        return RankingPositionResponse.builder()
                .position(position)
                .monasThisPeriod(periodMonas)
                .rankingOptIn(true)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .rankingType(type)
                .build();
    }

    private LocalDate getPeriodStart(RankingType type) {
        LocalDate today = LocalDate.now();
        return switch (type) {
            case WEEKLY -> today.with(DayOfWeek.MONDAY);
            case MONTHLY -> today.withDayOfMonth(1);
            case SEMESTER -> {
                int month = today.getMonthValue();
                // Semester 1: Feb–Jul  |  Semester 2: Aug–Jan
                if (month >= 2 && month <= 7) yield LocalDate.of(today.getYear(), 2, 1);
                else if (month >= 8)          yield LocalDate.of(today.getYear(), 8, 1);
                else                          yield LocalDate.of(today.getYear() - 1, 8, 1);
            }
        };
    }

    private LocalDate getPeriodEnd(RankingType type) {
        LocalDate today = LocalDate.now();
        return switch (type) {
            case WEEKLY -> today.with(DayOfWeek.SUNDAY);
            case MONTHLY -> today.withDayOfMonth(today.lengthOfMonth());
            case SEMESTER -> {
                int month = today.getMonthValue();
                if (month >= 2 && month <= 7) yield LocalDate.of(today.getYear(), 7, 31);
                else if (month >= 8)          yield LocalDate.of(today.getYear() + 1, 1, 31);
                else                          yield LocalDate.of(today.getYear(), 1, 31);
            }
        };
    }
}
