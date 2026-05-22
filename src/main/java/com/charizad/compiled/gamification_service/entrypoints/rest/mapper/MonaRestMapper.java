package com.charizad.compiled.gamification_service.entrypoints.rest.mapper;

import com.charizad.compiled.gamification_service.application.dto.request.AwardMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.request.CreateMonaRequest;
import org.springframework.stereotype.Component;

/**
 * Mapper para transformar parámetros del controller a DTOs de request.
 * Los DTOs de response se usan directamente desde la capa application.
 */
@Component
public class MonaRestMapper {

    public CreateMonaRequest toCreateRequest(CreateMonaRequest body) {
        return body;
    }

    public AwardMonaRequest toAwardRequest(String userId, String monaId) {
        return AwardMonaRequest.builder()
                .userId(userId)
                .monaId(monaId)
                .build();
    }

}
