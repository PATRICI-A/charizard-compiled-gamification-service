package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetMonasUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetMonasService implements GetMonasUseCase {

    private final BadgeRepositoryPort badgeRepository;
    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public List<MonaResponse> execute(String userId) {
        List<Badge> catalog = badgeRepository.findAllActive();
        Optional<UserGamification> userOpt = userGamificationRepository.findByUserId(userId);

        return catalog.stream()
                .map(badge -> buildMonaResponse(badge, userOpt.orElse(null)))
                .toList();
    }

    static MonaResponse buildMonaResponse(Badge badge, UserGamification user) {
        EarnedBadge earned = user == null ? null : user.getEarnedBadges().stream()
                .filter(b -> b.getBadgeId().equals(badge.getId()))
                .findFirst().orElse(null);

        BadgeProgress progress = user == null ? null : user.getProgress().stream()
                .filter(p -> p.getBadgeId().equals(badge.getId()))
                .findFirst().orElse(null);

        if (earned != null) {
            int target = progress != null ? progress.getRequiredValue() : 1;
            return MonaResponse.builder()
                    .monaId(badge.getId())
                    .name(badge.getName())
                    .description(badge.getDescription())
                    .rarity(badge.getCategory())
                    .unlocked(true)
                    .earnedAt(earned.getEarnedAt().toLocalDate())
                    .currentCount(target)
                    .targetCount(target)
                    .progressPercentage(100.0f)
                    .build();
        }

        int current = progress != null ? progress.getCurrentValue() : 0;
        int target = progress != null ? progress.getRequiredValue() : 1;
        float pct = target > 0 ? Math.min(current * 100.0f / target, 100.0f) : 0.0f;

        return MonaResponse.builder()
                .monaId(badge.getId())
                .name(badge.getName())
                .description(badge.getDescription())
                .rarity(badge.getCategory())
                .unlocked(false)
                .earnedAt(null)
                .currentCount(current)
                .targetCount(target)
                .progressPercentage(pct)
                .build();
    }
}
