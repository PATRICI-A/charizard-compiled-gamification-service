package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.BadgeProgressResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserProgressUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.BadgeProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserProgressService implements GetUserProgressUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;
    private final BadgeRepositoryPort badgeRepository;
    private final UserGamificationMapper userGamificationMapper;

    @Override
    public List<BadgeProgressResponse> execute(String userId) {
        UserGamification user = userGamificationRepository.findByUserId(userId)
                .orElseThrow(() -> new UserGamificationNotFoundException(userId));

        Map<String, BadgeProgress> progressMap = user.getProgress().stream()
                .collect(Collectors.toMap(BadgeProgress::getBadgeId, p -> p));

        return badgeRepository.findAllActive().stream()
                .map(badge -> {
                    BadgeProgress p = progressMap.get(badge.getId());
                    if (p != null) {
                        return userGamificationMapper.toProgressResponse(p);
                    }
                    return BadgeProgressResponse.builder()
                            .badgeId(badge.getId())
                            .currentValue(0)
                            .requiredValue(0)
                            .completed(false)
                            .percentageComplete(0)
                            .build();
                })
                .toList();
    }
}
