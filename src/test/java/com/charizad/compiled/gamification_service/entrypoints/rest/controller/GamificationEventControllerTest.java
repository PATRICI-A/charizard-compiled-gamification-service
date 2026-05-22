package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.BadgeUnlockEventRequest;
import com.charizad.compiled.gamification_service.application.dto.request.ZoneVisitedRequest;
import com.charizad.compiled.gamification_service.domain.ports.in.CheckBadgeUnlockUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GamificationEventControllerTest {

    @Mock private CheckBadgeUnlockUseCase checkBadgeUnlockUseCase;

    @InjectMocks private GamificationEventController controller;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new TestingAuthenticationToken("user-001", null));
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("POST /events/zone-visited returns 200 with awarded badges")
    void onZoneVisited_shouldReturn200() throws Exception {
        UUID badgeId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        ZoneVisitedRequest request = ZoneVisitedRequest.builder().campusZone("Biblioteca").build();

        when(checkBadgeUnlockUseCase.execute(any(BadgeUnlockEventRequest.class)))
                .thenReturn(List.of(badgeId));

        mockMvc.perform(post("/api/v1/gamificacion/events/zone-visited")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campusZone").value("Biblioteca"))
                .andExpect(jsonPath("$.awardedBadgeIds[0]").value(badgeId.toString()));
    }

    @Test
    @DisplayName("POST /events/zone-visited returns 200 with empty list when no badge awarded")
    void onZoneVisited_shouldReturnEmptyList() throws Exception {
        ZoneVisitedRequest request = ZoneVisitedRequest.builder().campusZone("Aulas").build();

        when(checkBadgeUnlockUseCase.execute(any(BadgeUnlockEventRequest.class)))
                .thenReturn(List.of());

        mockMvc.perform(post("/api/v1/gamificacion/events/zone-visited")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.awardedBadgeIds").isEmpty());
    }
}
