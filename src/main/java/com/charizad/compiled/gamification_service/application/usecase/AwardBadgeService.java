package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.BadgeNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardBadgeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.NotificationEventPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedBadge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AwardBadgeService implements AwardBadgeUseCase {

    private final BadgeRepositoryPort badgeRepository;
    private final UserGamificationRepositoryPort userGamificationRepository;
    private final NotificationEventPort notificationEventPort;
    private final UserGamificationMapper userGamificationMapper;

    @Override
    public EarnedBadgeResponse execute(AwardBadgeRequest request) {
        Badge badge = badgeRepository.findById(request.getBadgeId())
                .orElseThrow(() -> new BadgeNotFoundException(request.getBadgeId()));

        UserGamification user = userGamificationRepository.findByUserId(request.getUserId())
                .orElseGet(() -> UserGamification.newUser(request.getUserId()));

        // Si ya tiene la insignia, se ignora silenciosamente
        if (user.hasBadge(badge.getId())) {
            EarnedBadge existing = user.getEarnedBadges().stream()
                    .filter(b -> b.getBadgeId().equals(badge.getId()))
                    .findFirst()
                    .orElseThrow();
            return userGamificationMapper.toEarnedBadgeResponse(existing);
        }

        EarnedBadge earned = EarnedBadge.builder()
                .badgeId(badge.getId())
                .badgeName(badge.getName())
                .earnedAt(LocalDateTime.now())
                .xpAwarded(badge.getXpReward())
                .build();

        user.awardBadge(earned);
        userGamificationRepository.save(user);

        notificationEventPort.notifyBadgeEarned(request.getUserId(), badge);

        return userGamificationMapper.toEarnedBadgeResponse(earned);
    }
}
