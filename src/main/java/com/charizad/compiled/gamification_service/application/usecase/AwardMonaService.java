package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.MonaNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardMonaUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.CheckXpRewardsUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.NotificationEventPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedMona;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AwardMonaService implements AwardMonaUseCase {

    private final MonaRepositoryPort monaRepository;
    private final UserGamificationRepositoryPort userGamificationRepository;
    private final NotificationEventPort notificationEventPort;
    private final UserGamificationMapper userGamificationMapper;
    private final CheckXpRewardsUseCase checkXpRewardsUseCase;

    @Override
    public EarnedMonaResponse execute(AwardMonaRequest request) {
        Mona mona = monaRepository.findById(request.getMonaId())
                .orElseThrow(() -> new MonaNotFoundException(request.getMonaId()));

        UserGamification user = userGamificationRepository.findByUserId(request.getUserId())
                .orElseGet(() -> UserGamification.newUser(request.getUserId()));

        // Si ya tiene la insignia, se ignora silenciosamente
        if (user.hasMona(mona.getId())) {
            EarnedMona existing = user.getEarnedMonas().stream()
                    .filter(b -> b.getMonaId().equals(mona.getId()))
                    .findFirst()
                    .orElseThrow();
            return userGamificationMapper.toEarnedMonaResponse(existing);
        }

        EarnedMona earned = EarnedMona.builder()
                .monaId(mona.getId())
                .monaName(mona.getName())
                .earnedAt(LocalDateTime.now())
                .xpAwarded(mona.getXpReward())
                .build();

        user.awardMona(earned);
        userGamificationRepository.save(user);

        // Check whether the newly gained XP crosses any reward threshold
        checkXpRewardsUseCase.checkAndUnlock(user);

        // Publish async notification via RabbitMQ
        notificationEventPort.notifyMonaEarned(request.getUserId(), mona);

        return userGamificationMapper.toEarnedMonaResponse(earned);
    }
}
