package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.CreateMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.application.mapper.MonaMapper;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.ports.in.CreateMonaUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateMonaService implements CreateMonaUseCase {

    private final MonaRepositoryPort MonaRepository;
    private final MonaMapper MonaMapper;

    @Override
    public MonaResponse execute(CreateMonaRequest request) {
        Mona Mona = MonaMapper.toDomain(request);
        Mona saved = MonaRepository.save(Mona);
        return MonaMapper.toResponse(saved);
    }
}
