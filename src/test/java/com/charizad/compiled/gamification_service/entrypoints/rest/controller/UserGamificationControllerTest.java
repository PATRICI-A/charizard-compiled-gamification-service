package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.ports.in.*;
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
import java.util.Map;

import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserGamificationControllerTest {

    @Mock GetUserBadgesUseCase getUserBadgesUseCase;
    @Mock GetUserProgressUseCase getUserProgressUseCase;
    @Mock GetUserStatsUseCase getUserStatsUseCase;
    @Mock ToggleRankingOptInUseCase toggleRankingOptInUseCase;
    @Mock GetRankingUseCase getRankingUseCase;
    @Mock GetUserRewardsUseCase getUserRewardsUseCase;
    @Mock GetUserLevelUseCase getUserLevelUseCase;
    @Mock GetRankingPositionUseCase getRankingPositionUseCase;

    @InjectMocks UserGamificationController controller;

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
    @DisplayName("GET /me/badges retorna 200 con lista de insignias")
    void getMyBadges_shouldReturn200() throws Exception {
        List<EarnedBadgeResponse> badges = List.of(
                EarnedBadgeResponse.builder()
                        .badgeId("badge-001")
                        .badgeName("Primer Parche")
                        .earnedAt(LocalDateTime.now())
                        .xpAwarded(100)
                        .build()
        );

        when(getUserBadgesUseCase.execute(any())).thenReturn(badges);

        mockMvc.perform(get("/api/v1/gamificacion/me/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].badgeId").value("badge-001"))
                .andExpect(jsonPath("$[0].xpAwarded").value(100));
    }

    @Test
    @DisplayName("GET /me/badges retorna 404 si no existe el perfil")
    void getMyBadges_shouldReturn404_whenProfileNotFound() throws Exception {
        when(getUserBadgesUseCase.execute(any()))
                .thenThrow(new UserGamificationNotFoundException("user-999"));

        mockMvc.perform(get("/api/v1/gamificacion/me/badges"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Perfil de gamificación no encontrado para el usuario: user-999"));
    }

    @Test
    @DisplayName("GET /me/stats retorna 200 con estadísticas del usuario")
    void getMyStats_shouldReturn200() throws Exception {
        UserStatsResponse stats = UserStatsResponse.builder()
                .userId("user-001")
                .totalXp(350)
                .weeklyXp(150)
                .rankingOptIn(true)
                .totalBadgesEarned(2)
                .build();

        when(getUserStatsUseCase.execute(any())).thenReturn(stats);

        mockMvc.perform(get("/api/v1/gamificacion/me/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalXp").value(350))
                .andExpect(jsonPath("$.weeklyXp").value(150))
                .andExpect(jsonPath("$.rankingOptIn").value(true))
                .andExpect(jsonPath("$.totalBadgesEarned").value(2));
    }

    @Test
    @DisplayName("PATCH /me/ranking/optin retorna 200 con nuevo estado")
    void setRankingOptIn_shouldReturn200() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user-001", null));
        when(toggleRankingOptInUseCase.execute("user-001", true)).thenReturn(true);

        mockMvc.perform(patch("/api/v1/gamificacion/me/ranking/optin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("participar", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rankingOptIn").value(true))
                .andExpect(jsonPath("$.message").value("Ahora participas en el ranking semanal."));
    }

    @Test
    @DisplayName("GET /ranking returns 200 with weekly ranking")
    void getRanking_shouldReturn200() throws Exception {
        List<RankingEntryResponse> ranking = List.of(
                RankingEntryResponse.builder().position(1).userId("user-A").monasThisPeriod(5).totalBadgesEarned(3).build(),
                RankingEntryResponse.builder().position(2).userId("user-B").monasThisPeriod(3).totalBadgesEarned(2).build()
        );

        when(getRankingUseCase.execute(any(RankingType.class))).thenReturn(ranking);

        mockMvc.perform(get("/api/v1/gamificacion/ranking?type=WEEKLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].position").value(1))
                .andExpect(jsonPath("$[0].userId").value("user-A"))
                .andExpect(jsonPath("$[0].monasThisPeriod").value(5))
                .andExpect(jsonPath("$[1].position").value(2));
    }

    @Test
    @DisplayName("GET /ranking defaults to WEEKLY when no type param")
    void getRanking_shouldDefaultToWeekly() throws Exception {
        when(getRankingUseCase.execute(RankingType.WEEKLY)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/gamificacion/ranking"))
                .andExpect(status().isOk());
    }
}
