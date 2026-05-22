package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.AwardMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.request.CreateMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.MonaNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardMonaUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.CreateMonaUseCase;
import com.charizad.compiled.gamification_service.entrypoints.advice.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MonaControllerTest {

    @Mock CreateMonaUseCase createMonaUseCase;
    @Mock AwardMonaUseCase awardMonaUseCase;

    @InjectMocks MonaController controller;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/gamificacion/Monas retorna 201 con la insignia creada")
    void createMona_shouldReturn201() throws Exception {
        CreateMonaRequest request = CreateMonaRequest.builder()
                .name("El Legendario")
                .description("Insignia legendaria")
                .category(MonaCategory.LEGENDARY)
                .xpReward(500)
                .build();

        MonaResponse response = MonaResponse.builder()
                .id("Mona-001")
                .name("El Legendario")
                .category(MonaCategory.LEGENDARY)
                .xpReward(500)
                .createdAt(LocalDateTime.now())
                .build();

        when(createMonaUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/gamificacion/Monas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("Mona-001"))
                .andExpect(jsonPath("$.name").value("El Legendario"))
                .andExpect(jsonPath("$.xpReward").value(500));
    }

    @Test
    @DisplayName("POST /api/v1/gamificacion/Monas retorna 400 si faltan campos obligatorios")
    void createMona_shouldReturn400_whenMissingFields() throws Exception {
        String invalidBody = """
                {
                    "name": "",
                    "xpReward": 0
                }
                """;

        mockMvc.perform(post("/api/v1/gamificacion/Monas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/gamificacion/Monas/award retorna 200 al otorgar insignia")
    void awardMona_shouldReturn200() throws Exception {
        AwardMonaRequest request = AwardMonaRequest.builder()
                .userId("user-001")
                .monaId("Mona-001")
                .build();

        EarnedMonaResponse response = EarnedMonaResponse.builder()
                .monaId("Mona-001")
                .monaName("El Legendario")
                .xpAwarded(500)
                .build();

        when(awardMonaUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/gamificacion/Monas/award")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monaId").value("Mona-001"))
                .andExpect(jsonPath("$.xpAwarded").value(500));
    }

    @Test
    @DisplayName("POST /api/v1/gamificacion/Monas/award retorna 404 si la insignia no existe")
    void awardMona_shouldReturn404_whenMonaNotFound() throws Exception {
        AwardMonaRequest request = AwardMonaRequest.builder()
                .userId("user-001")
                .monaId("Mona-999")
                .build();

        when(awardMonaUseCase.execute(any()))
                .thenThrow(new MonaNotFoundException("Mona-999"));

        mockMvc.perform(post("/api/v1/gamificacion/Monas/award")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Insignia no encontrada con id: Mona-999"));
    }
}
