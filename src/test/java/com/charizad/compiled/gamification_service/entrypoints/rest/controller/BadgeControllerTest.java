package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.request.CreateBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.BadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.BadgeNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardBadgeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.CreateBadgeUseCase;
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
class BadgeControllerTest {

    @Mock CreateBadgeUseCase createBadgeUseCase;
    @Mock AwardBadgeUseCase awardBadgeUseCase;

    @InjectMocks BadgeController controller;

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
    @DisplayName("POST /api/v1/gamificacion/badges retorna 201 con la insignia creada")
    void createBadge_shouldReturn201() throws Exception {
        CreateBadgeRequest request = CreateBadgeRequest.builder()
                .name("El Legendario")
                .description("Insignia legendaria")
                .category(BadgeCategory.LEGENDARY)
                .xpReward(500)
                .build();

        BadgeResponse response = BadgeResponse.builder()
                .id("badge-001")
                .name("El Legendario")
                .category(BadgeCategory.LEGENDARY)
                .xpReward(500)
                .createdAt(LocalDateTime.now())
                .build();

        when(createBadgeUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/gamificacion/badges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("badge-001"))
                .andExpect(jsonPath("$.name").value("El Legendario"))
                .andExpect(jsonPath("$.xpReward").value(500));
    }

    @Test
    @DisplayName("POST /api/v1/gamificacion/badges retorna 400 si faltan campos obligatorios")
    void createBadge_shouldReturn400_whenMissingFields() throws Exception {
        String invalidBody = """
                {
                    "name": "",
                    "xpReward": 0
                }
                """;

        mockMvc.perform(post("/api/v1/gamificacion/badges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/gamificacion/badges/award retorna 200 al otorgar insignia")
    void awardBadge_shouldReturn200() throws Exception {
        AwardBadgeRequest request = AwardBadgeRequest.builder()
                .userId("user-001")
                .badgeId("badge-001")
                .build();

        EarnedBadgeResponse response = EarnedBadgeResponse.builder()
                .badgeId("badge-001")
                .badgeName("El Legendario")
                .xpAwarded(500)
                .build();

        when(awardBadgeUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/gamificacion/badges/award")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.badgeId").value("badge-001"))
                .andExpect(jsonPath("$.xpAwarded").value(500));
    }

    @Test
    @DisplayName("POST /api/v1/gamificacion/badges/award retorna 404 si la insignia no existe")
    void awardBadge_shouldReturn404_whenBadgeNotFound() throws Exception {
        AwardBadgeRequest request = AwardBadgeRequest.builder()
                .userId("user-001")
                .badgeId("badge-999")
                .build();

        when(awardBadgeUseCase.execute(any()))
                .thenThrow(new BadgeNotFoundException("badge-999"));

        mockMvc.perform(post("/api/v1/gamificacion/badges/award")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Insignia no encontrada con id: badge-999"));
    }
}
