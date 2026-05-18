package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.CreateRewardRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedRewardResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RewardResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import com.charizad.compiled.gamification_service.domain.ports.in.CreateRewardUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserRewardsUseCase;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RewardControllerTest {

    @Mock CreateRewardUseCase createRewardUseCase;
    @Mock GetUserRewardsUseCase getUserRewardsUseCase;

    @InjectMocks RewardController controller;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST /api/v1/rewards retorna 201 con el reward creado")
    void createReward_shouldReturn201() throws Exception {
        CreateRewardRequest request = CreateRewardRequest.builder()
                .name("Gold Title")
                .description("Un título dorado")
                .type(RewardType.TITLE)
                .xpThreshold(500)
                .build();

        RewardResponse response = RewardResponse.builder()
                .id("reward-001")
                .name("Gold Title")
                .description("Un título dorado")
                .type(RewardType.TITLE)
                .xpThreshold(500)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(createRewardUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("reward-001"))
                .andExpect(jsonPath("$.name").value("Gold Title"));
    }

    @Test
    @DisplayName("POST /api/v1/rewards retorna 400 si faltan campos")
    void createReward_shouldReturn400_whenMissingFields() throws Exception {
        String invalidBody = """
                {
                    "name": "",
                    "xpThreshold": 0
                }
                """;

        mockMvc.perform(post("/api/v1/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/rewards/me retorna rewards del usuario autenticado")
    void getMyRewards_shouldReturn200() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user-001", null));
        EarnedRewardResponse reward = EarnedRewardResponse.builder()
                .rewardId("reward-001")
                .rewardName("Gold Title")
                .rewardType(RewardType.TITLE)
                .unlockedAt(LocalDateTime.now())
                .xpAtUnlock(1000)
                .build();

        when(getUserRewardsUseCase.execute("user-001")).thenReturn(List.of(reward));

        mockMvc.perform(get("/api/v1/rewards/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rewardId").value("reward-001"))
                .andExpect(jsonPath("$[0].rewardName").value("Gold Title"));
    }

    @Test
    @DisplayName("GET /api/v1/rewards/me retorna 404 si el perfil no existe")
    void getMyRewards_shouldReturn404_whenProfileNotFound() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user-999", null));
        when(getUserRewardsUseCase.execute("user-999"))
                .thenThrow(new UserGamificationNotFoundException("user-999"));

        mockMvc.perform(get("/api/v1/rewards/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Perfil de gamificación no encontrado para el usuario: user-999"));
    }
}
