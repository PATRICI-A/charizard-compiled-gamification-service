package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.response.MonaDetailResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.MonaNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.GetMonaByIdUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetMonaByIdService implements GetMonaByIdUseCase {

    private final MonaRepositoryPort MonaRepository;
    private final UserGamificationRepositoryPort userGamificationRepository;

    @Override
    public MonaDetailResponse execute(String userId, String monaId) {
        Mona Mona = MonaRepository.findById(monaId)
                .orElseThrow(() -> new MonaNotFoundException(monaId));

        Optional<UserGamification> userOpt = userGamificationRepository.findByUserId(userId);

        return GetMonasService.buildMonaDetailResponse(Mona, userOpt.orElse(null));
    }
}
