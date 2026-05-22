package com.charizad.compiled.gamification_service.application.mapper;

import com.charizad.compiled.gamification_service.application.dto.request.CreateMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MonaMapper {

    public Mona toDomain(CreateMonaRequest request) {
        return Mona.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .xpReward(request.getXpReward())
                .iconUrl(request.getIconUrl())
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    public MonaResponse toResponse(Mona Mona) {
        return MonaResponse.builder()
                .id(Mona.getId())
                .name(Mona.getName())
                .description(Mona.getDescription())
                .category(Mona.getCategory())
                .xpReward(Mona.getXpReward())
                .iconUrl(Mona.getIconUrl())
                .createdAt(Mona.getCreatedAt())
                .build();
    }
}
