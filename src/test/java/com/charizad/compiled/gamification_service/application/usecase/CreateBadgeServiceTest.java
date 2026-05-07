package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.CreateBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.BadgeResponse;
import com.charizad.compiled.gamification_service.application.mapper.BadgeMapper;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBadgeServiceTest {

    @Mock private BadgeRepositoryPort badgeRepository;
    @Mock private BadgeMapper badgeMapper;

    @InjectMocks
    private CreateBadgeService service;

    @Test
    @DisplayName("Crear insignia guarda y retorna el response correctamente")
    void execute_shouldSaveAndReturnBadgeResponse() {
        CreateBadgeRequest request = CreateBadgeRequest.builder()
                .name("El Legendario")
                .description("Insignia legendaria")
                .category(BadgeCategory.LEGENDARY)
                .xpReward(500)
                .build();

        Badge domain = Badge.builder()
                .name("El Legendario")
                .category(BadgeCategory.LEGENDARY)
                .xpReward(500)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        Badge saved = Badge.builder()
                .id("badge-new")
                .name("El Legendario")
                .category(BadgeCategory.LEGENDARY)
                .xpReward(500)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        BadgeResponse expected = BadgeResponse.builder()
                .id("badge-new")
                .name("El Legendario")
                .category(BadgeCategory.LEGENDARY)
                .xpReward(500)
                .build();

        when(badgeMapper.toDomain(request)).thenReturn(domain);
        when(badgeRepository.save(domain)).thenReturn(saved);
        when(badgeMapper.toResponse(saved)).thenReturn(expected);

        BadgeResponse result = service.execute(request);

        assertThat(result.getId()).isEqualTo("badge-new");
        assertThat(result.getName()).isEqualTo("El Legendario");
        assertThat(result.getXpReward()).isEqualTo(500);
        verify(badgeRepository).save(domain);
    }
}
