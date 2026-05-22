package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.CreateMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.application.mapper.MonaMapper;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
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
class CreateMonaServiceTest {

    @Mock private MonaRepositoryPort MonaRepository;
    @Mock private MonaMapper MonaMapper;

    @InjectMocks
    private CreateMonaService service;

    @Test
    @DisplayName("Crear insignia guarda y retorna el response correctamente")
    void execute_shouldSaveAndReturnMonaResponse() {
        CreateMonaRequest request = CreateMonaRequest.builder()
                .name("El Legendario")
                .description("Insignia legendaria")
                .category(MonaCategory.LEGENDARY)
                .xpReward(500)
                .build();

        Mona domain = Mona.builder()
                .name("El Legendario")
                .category(MonaCategory.LEGENDARY)
                .xpReward(500)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        Mona saved = Mona.builder()
                .id("Mona-new")
                .name("El Legendario")
                .category(MonaCategory.LEGENDARY)
                .xpReward(500)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        MonaResponse expected = MonaResponse.builder()
                .id("Mona-new")
                .name("El Legendario")
                .category(MonaCategory.LEGENDARY)
                .xpReward(500)
                .build();

        when(MonaMapper.toDomain(request)).thenReturn(domain);
        when(MonaRepository.save(domain)).thenReturn(saved);
        when(MonaMapper.toResponse(saved)).thenReturn(expected);

        MonaResponse result = service.execute(request);

        assertThat(result.getId()).isEqualTo("Mona-new");
        assertThat(result.getName()).isEqualTo("El Legendario");
        assertThat(result.getXpReward()).isEqualTo(500);
        verify(MonaRepository).save(domain);
    }
}
