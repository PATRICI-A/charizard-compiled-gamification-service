package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.MonaNotFoundException;
import com.charizad.compiled.gamification_service.domain.exceptions.InvalidEventCodeException;
import com.charizad.compiled.gamification_service.domain.model.EventCode;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardMonaUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.RedeemEventCodeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.EventCodeRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedeemEventCodeService implements RedeemEventCodeUseCase {

    private static final String Mona_ASISTENTE = "Asistente";

    private final EventCodeRepositoryPort eventCodeRepository;
    private final MonaRepositoryPort MonaRepository;
    private final AwardMonaUseCase awardMonaUseCase;

    @Override
    public EarnedMonaResponse execute(String userId, String eventCode) {
        EventCode code = eventCodeRepository.findByCode(eventCode)
                .orElseThrow(InvalidEventCodeException::new);

        if (!code.isValid(LocalDateTime.now()) || code.isUsedBy(userId)) {
            throw new InvalidEventCodeException();
        }

        String MonaId = MonaRepository.findByName(Mona_ASISTENTE)
                .orElseThrow(() -> new MonaNotFoundException(Mona_ASISTENTE))
                .getId();

        code.markUsedBy(userId);
        eventCodeRepository.save(code);

        log.info("[RedeemEventCode] userId={} canjeó código='{}' → mona Asistente", userId, eventCode);

        return awardMonaUseCase.execute(AwardMonaRequest.builder()
                .userId(userId)
                .monaId(MonaId)
                .build());
    }
}
