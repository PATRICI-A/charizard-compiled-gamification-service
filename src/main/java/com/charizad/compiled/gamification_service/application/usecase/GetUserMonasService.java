package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserMonasUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserMonasService implements GetUserMonasUseCase {

    private final UserGamificationRepositoryPort userGamificationRepository;
    private final UserGamificationMapper userGamificationMapper;

    @Override
    public List<EarnedMonaResponse> execute(String userId) {
        UserGamification user = userGamificationRepository.findByUserId(userId)
                .orElseThrow(() -> new UserGamificationNotFoundException(userId));

        return user.getEarnedMonas().stream()
                .map(userGamificationMapper::toEarnedMonaResponse)
                .toList();
    }
}
