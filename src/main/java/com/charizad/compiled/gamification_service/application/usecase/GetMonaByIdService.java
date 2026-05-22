package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.BadgeNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetMonaByIdUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMonaByIdService implements GetMonaByIdUseCase {

    private final BadgeRepositoryPort badgeRepository;
    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public MonaResponse execute(String userId, String monaId) {
        UUID id = UUID.fromString(monaId);
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new BadgeNotFoundException(id));

        Optional<UserGamification> userOpt = userGamificationRepository.findByUserId(userId);

        return GetMonasService.buildMonaResponse(badge, userOpt.orElse(null));
    }
}
