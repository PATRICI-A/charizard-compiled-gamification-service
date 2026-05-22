package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.BadgeNotFoundException;
import com.charizad.compiled.gamification_service.domain.exceptions.InvalidEventCodeException;
import com.charizad.compiled.gamification_service.domain.model.EventCode;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardBadgeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.RedeemEventCodeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.EventCodeRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedeemEventCodeService implements RedeemEventCodeUseCase {

    private static final String BADGE_ASISTENTE = "Asistente";

    private final EventCodeRepositoryPort eventCodeRepository;
    private final BadgeRepositoryPort badgeRepository;
    private final AwardBadgeUseCase awardBadgeUseCase;

    @Override
    public EarnedBadgeResponse execute(String userId, String eventCode) {
        EventCode code = eventCodeRepository.findByCode(eventCode)
                .orElseThrow(InvalidEventCodeException::new);

        if (!code.isValid(LocalDateTime.now()) || code.isUsedBy(userId)) {
            throw new InvalidEventCodeException();
        }

        UUID badgeId = badgeRepository.findByName(BADGE_ASISTENTE)
                .orElseThrow(() -> new BadgeNotFoundException(BADGE_ASISTENTE))
                .getId();

        code.markUsedBy(userId);
        eventCodeRepository.save(code);

        log.info("[RedeemEventCode] userId={} canjeó código='{}' → mona Asistente", userId, eventCode);

        return awardBadgeUseCase.execute(AwardBadgeRequest.builder()
                .userId(userId)
                .badgeId(badgeId)
                .build());
    }
}
