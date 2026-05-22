package com.charizad.compiled.gamification_service.domain.ports.in;

import com.charizad.compiled.gamification_service.application.dto.request.CreateMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;

public interface CreateMonaUseCase {
    MonaResponse execute(CreateMonaRequest request);
}
