package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserLevelResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserBadgesUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserLevelUseCase;
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
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InternalAnalyticsControllerTest {

    @Mock GetUserBadgesUseCase getUserBadgesUseCase;
    @Mock GetUserLevelUseCase getUserLevelUseCase;

    @InjectMocks InternalAnalyticsController controller;

    MockMvc mockMvc;
    final String userId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /internal/user/{userId}/achievements retorna 200 con lista de logros")
    void getUserAchievements_retorna200ConLogros() throws Exception {
        EarnedBadgeResponse badge = EarnedBadgeResponse.builder()
                .badgeId(UUID.randomUUID())
                .badgeName("Primera Conexión")
                .earnedAt(LocalDateTime.now())
                .xpAwarded(10)
                .build();
        when(getUserBadgesUseCase.execute(userId)).thenReturn(List.of(badge));

        mockMvc.perform(get("/api/v1/gamificacion/internal/user/{userId}/achievements", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].badgeName").value("Primera Conexión"))
                .andExpect(jsonPath("$[0].xpAwarded").value(10));
    }

    @Test
    @DisplayName("GET /internal/user/{userId}/achievements retorna 200 con lista vacía cuando no hay logros")
    void getUserAchievements_retorna200ConListaVacia() throws Exception {
        when(getUserBadgesUseCase.execute(userId)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/gamificacion/internal/user/{userId}/achievements", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /internal/user/{userId}/achievements retorna 404 cuando no existe perfil")
    void getUserAchievements_retorna404SiNoHayPerfil() throws Exception {
        when(getUserBadgesUseCase.execute(userId))
                .thenThrow(new UserGamificationNotFoundException(userId));

        mockMvc.perform(get("/api/v1/gamificacion/internal/user/{userId}/achievements", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /internal/user/{userId}/level retorna 200 con datos de nivel")
    void getUserLevel_retorna200ConNivel() throws Exception {
        UserLevelResponse level = UserLevelResponse.builder()
                .userId(userId)
                .nivel(2)
                .levelName(null)
                .totalMonas(4)
                .monasParaSiguienteNivel(2)
                .build();
        when(getUserLevelUseCase.execute(userId)).thenReturn(level);

        mockMvc.perform(get("/api/v1/gamificacion/internal/user/{userId}/level", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivel").value(2))
                .andExpect(jsonPath("$.totalMonas").value(4))
                .andExpect(jsonPath("$.monasParaSiguienteNivel").value(2));
    }

    @Test
    @DisplayName("GET /internal/user/{userId}/level retorna 404 cuando no existe perfil")
    void getUserLevel_retorna404SiNoHayPerfil() throws Exception {
        when(getUserLevelUseCase.execute(userId))
                .thenThrow(new UserGamificationNotFoundException(userId));

        mockMvc.perform(get("/api/v1/gamificacion/internal/user/{userId}/level", userId))
                .andExpect(status().isNotFound());
    }
}
