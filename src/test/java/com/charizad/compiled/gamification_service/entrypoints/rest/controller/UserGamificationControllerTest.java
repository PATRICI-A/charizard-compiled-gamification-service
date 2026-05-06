package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.ports.in.*;
import com.charizad.compiled.gamification_service.entrypoints.advice.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @InjectMocks UserGamificationController controller;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
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

        mockMvc.perform(get("/api/v1/gamification/me/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].badgeId").value("badge-001"))
                .andExpect(jsonPath("$[0].xpAwarded").value(100));
    }

    @Test
    @DisplayName("GET /me/badges retorna 404 si no existe el perfil")
    void getMyBadges_shouldReturn404_whenProfileNotFound() throws Exception {
        when(getUserBadgesUseCase.execute(any()))
                .thenThrow(new UserGamificationNotFoundException("user-999"));

        mockMvc.perform(get("/api/v1/gamification/me/badges"))
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

        mockMvc.perform(get("/api/v1/gamification/me/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalXp").value(350))
                .andExpect(jsonPath("$.weeklyXp").value(150))
                .andExpect(jsonPath("$.rankingOptIn").value(true))
                .andExpect(jsonPath("$.totalBadgesEarned").value(2));
    }

    @Test
    @DisplayName("PATCH /me/ranking/toggle retorna 200 con nuevo estado")
    void toggleRanking_shouldReturn200() throws Exception {
        when(toggleRankingOptInUseCase.execute(any())).thenReturn(true);

        mockMvc.perform(patch("/api/v1/gamification/me/ranking/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rankingOptIn").value(true))
                .andExpect(jsonPath("$.message").value("Ahora participas en el ranking semanal."));
    }

    @Test
    @DisplayName("GET /ranking retorna 200 con el ranking semanal")
    void getRanking_shouldReturn200() throws Exception {
        List<RankingEntryResponse> ranking = List.of(
                RankingEntryResponse.builder().position(1).userId("user-A").weeklyXp(500).totalBadgesEarned(3).build(),
                RankingEntryResponse.builder().position(2).userId("user-B").weeklyXp(300).totalBadgesEarned(2).build()
        );

        when(getRankingUseCase.execute(anyInt())).thenReturn(ranking);

        mockMvc.perform(get("/api/v1/gamification/ranking?limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].position").value(1))
                .andExpect(jsonPath("$[0].userId").value("user-A"))
                .andExpect(jsonPath("$[0].weeklyXp").value(500))
                .andExpect(jsonPath("$[1].position").value(2));
    }

    @Test
    @DisplayName("GET /ranking usa limit=10 por defecto")
    void getRanking_shouldUseDefaultLimit() throws Exception {
        when(getRankingUseCase.execute(10)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/gamification/ranking"))
                .andExpect(status().isOk());
    }
}
