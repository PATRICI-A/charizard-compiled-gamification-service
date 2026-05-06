package com.charizad.compiled.gamification_service.application.service;

import com.charizad.compiled.gamification_service.application.dto.response.BadgeProgressResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserProgressUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserProgressService implements GetUserProgressUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;
    private final UserGamificationMapper userGamificationMapper;

    @Override
    public List<BadgeProgressResponse> execute(String userId) {
        UserGamification user = userGamificationRepository.findByUserId(userId)
                .orElseThrow(() -> new UserGamificationNotFoundException(userId));

        return user.getProgress().stream()
                .map(userGamificationMapper::toProgressResponse)
                .toList();
    }
}
