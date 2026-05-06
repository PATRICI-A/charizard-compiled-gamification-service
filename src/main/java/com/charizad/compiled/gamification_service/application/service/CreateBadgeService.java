package com.charizad.compiled.gamification_service.application.service;

import com.charizad.compiled.gamification_service.application.dto.request.CreateBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.BadgeResponse;
import com.charizad.compiled.gamification_service.application.mapper.BadgeMapper;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.ports.in.CreateBadgeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateBadgeService implements CreateBadgeUseCase {

    private final BadgeRepositoryPort badgeRepository;
    private final BadgeMapper badgeMapper;

    @Override
    public BadgeResponse execute(CreateBadgeRequest request) {
        Badge badge = badgeMapper.toDomain(request);
        Badge saved = badgeRepository.save(badge);
        return badgeMapper.toResponse(saved);
    }
}
