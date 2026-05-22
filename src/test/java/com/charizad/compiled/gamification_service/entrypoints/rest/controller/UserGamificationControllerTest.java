package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.response.*;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @Mock RedeemEventCodeUseCase redeemEventCodeUseCase;
    @Mock GetMonasUseCase getMonasUseCase;
    @Mock GetMonaByIdUseCase getMonaByIdUseCase;

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
    @DisplayName("GET /me/badges returns 200 with badge list")
    void getMyBadges_shouldReturn200() throws Exception {
        List<EarnedBadgeResponse> badges = List.of(
                EarnedBadgeResponse.builder()
                        .badgeId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                        .badgeName("Primer Parche")
                        .earnedAt(LocalDateTime.now())
                        .xpAwarded(100)
                        .build()
        );

        when(getUserBadgesUseCase.execute(any())).thenReturn(badges);

        mockMvc.perform(get("/api/v1/gamificacion/me/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].badgeId").value("00000000-0000-0000-0000-000000000001"))
                .andExpect(jsonPath("$[0].xpAwarded").value(100));
    }

    @Test
    @DisplayName("GET /me/badges returns 404 when profile not found")
    void getMyBadges_shouldReturn404_whenProfileNotFound() throws Exception {
        when(getUserBadgesUseCase.execute(any()))
                .thenThrow(new UserGamificationNotFoundException("user-999"));

        mockMvc.perform(get("/api/v1/gamificacion/me/badges"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Perfil de gamificación no encontrado para el usuario: user-999"));
    }

    @Test
    @DisplayName("GET /me/stats returns 200 with user statistics")
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
    @DisplayName("PATCH /me/ranking/optin returns 200 with studentId, rankingOptIn and updatedAt")
    void setRankingOptIn_shouldReturn200() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user-001", null));

        RankingOptInResponse response = RankingOptInResponse.builder()
                .studentId("user-001")
                .rankingOptIn(true)
                .updatedAt(LocalDateTime.of(2026, 5, 21, 10, 0))
                .build();

        when(toggleRankingOptInUseCase.execute(eq("user-001"), eq(true))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/gamificacion/me/ranking/optin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("participe", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value("user-001"))
                .andExpect(jsonPath("$.rankingOptIn").value(true))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("GET /ranking returns 200 with weekly ranking using tipo=semanal")
    void getRanking_shouldReturn200() throws Exception {
        List<RankingEntryResponse> ranking = List.of(
                RankingEntryResponse.builder().position(1).studentId("user-A").monasThisPeriod(5).build(),
                RankingEntryResponse.builder().position(2).studentId("user-B").monasThisPeriod(3).build()
        );

        when(getRankingUseCase.execute(any(RankingType.class))).thenReturn(ranking);

        mockMvc.perform(get("/api/v1/gamificacion/ranking?tipo=semanal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].position").value(1))
                .andExpect(jsonPath("$[0].studentId").value("user-A"))
                .andExpect(jsonPath("$[0].monasThisPeriod").value(5))
                .andExpect(jsonPath("$[1].position").value(2));
    }

    @Test
    @DisplayName("GET /ranking defaults to WEEKLY when no tipo param")
    void getRanking_shouldDefaultToWeekly() throws Exception {
        when(getRankingUseCase.execute(RankingType.WEEKLY)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/gamificacion/ranking"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /monas/evento returns 200 with earned badge")
    void redeemEventCode_shouldReturn200() throws Exception {
        EarnedBadgeResponse badge = EarnedBadgeResponse.builder()
                .badgeId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .badgeName("Asistente")
                .earnedAt(LocalDateTime.now())
                .xpAwarded(50)
                .build();

        when(redeemEventCodeUseCase.execute(any(), eq("CODE123"))).thenReturn(badge);

        mockMvc.perform(post("/api/v1/gamificacion/monas/evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("eventCode", "CODE123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.badgeName").value("Asistente"))
                .andExpect(jsonPath("$.xpAwarded").value(50));
    }

    @Test
    @DisplayName("GET /monas returns 200 with mona list")
    void getMonas_shouldReturn200() throws Exception {
        MonaResponse mona = MonaResponse.builder()
                .monaId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .name("Explorador I")
                .unlocked(true)
                .rarity(BadgeCategory.COMMON)
                .progressPercentage(100f)
                .build();

        when(getMonasUseCase.execute(any())).thenReturn(List.of(mona));

        mockMvc.perform(get("/api/v1/gamificacion/monas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Explorador I"))
                .andExpect(jsonPath("$[0].unlocked").value(true));
    }

    @Test
    @DisplayName("GET /monas/{monaId} returns 200 with mona detail")
    void getMonaById_shouldReturn200() throws Exception {
        MonaResponse mona = MonaResponse.builder()
                .monaId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .name("Explorador II")
                .unlocked(false)
                .rarity(BadgeCategory.EPIC)
                .currentCount(2)
                .targetCount(5)
                .progressPercentage(40f)
                .build();

        when(getMonaByIdUseCase.execute(any(), eq("mona-001"))).thenReturn(mona);

        mockMvc.perform(get("/api/v1/gamificacion/monas/mona-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Explorador II"))
                .andExpect(jsonPath("$.progressPercentage").value(40.0));
    }

    @Test
    @DisplayName("GET /me/progress returns 200 with progress list")
    void getMyProgress_shouldReturn200() throws Exception {
        BadgeProgressResponse progress = BadgeProgressResponse.builder()
                .badgeId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .currentValue(3)
                .requiredValue(5)
                .completed(false)
                .percentageComplete(60)
                .build();

        when(getUserProgressUseCase.execute(any())).thenReturn(List.of(progress));

        mockMvc.perform(get("/api/v1/gamificacion/me/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentValue").value(3))
                .andExpect(jsonPath("$[0].requiredValue").value(5));
    }

    @Test
    @DisplayName("GET /me/rewards returns 200 with reward list")
    void getMyRewards_shouldReturn200() throws Exception {
        EarnedRewardResponse reward = EarnedRewardResponse.builder()
                .rewardId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .rewardName("Gold Title")
                .rewardType(com.charizad.compiled.gamification_service.domain.model.enums.RewardType.TITLE)
                .xpAtUnlock(1000)
                .unlockedAt(LocalDateTime.now())
                .build();

        when(getUserRewardsUseCase.execute(any())).thenReturn(List.of(reward));

        mockMvc.perform(get("/api/v1/gamificacion/me/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rewardName").value("Gold Title"))
                .andExpect(jsonPath("$[0].xpAtUnlock").value(1000));
    }

    @Test
    @DisplayName("GET /ranking/mi-posicion returns 200 with position")
    void getMyPosition_shouldReturn200() throws Exception {
        RankingPositionResponse pos = RankingPositionResponse.builder()
                .position(3)
                .monasThisPeriod(8)
                .rankingOptIn(true)
                .periodStart(LocalDate.of(2026, 5, 18))
                .periodEnd(LocalDate.of(2026, 5, 24))
                .rankingType(RankingType.WEEKLY)
                .build();

        when(getRankingPositionUseCase.execute(any(), any())).thenReturn(pos);

        mockMvc.perform(get("/api/v1/gamificacion/ranking/mi-posicion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value(3))
                .andExpect(jsonPath("$.monasThisPeriod").value(8));
    }

    @Test
    @DisplayName("GET /me/nivel returns 200 with level info")
    void getMiNivel_shouldReturn200() throws Exception {
        UserLevelResponse level = UserLevelResponse.builder()
                .userId("user-001")
                .nivel(5)
                .levelName("Experto")
                .totalMonas(25)
                .monasParaSiguienteNivel(5)
                .build();

        when(getUserLevelUseCase.execute(any())).thenReturn(level);

        mockMvc.perform(get("/api/v1/gamificacion/me/nivel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivel").value(5))
                .andExpect(jsonPath("$.levelName").value("Experto"));
    }

    @Test
    @DisplayName("GET /ranking/mi-posicion with tipo=mensual uses MONTHLY type")
    void getMyPosition_withMonthlyParam() throws Exception {
        RankingPositionResponse pos = RankingPositionResponse.builder()
                .position(1).monasThisPeriod(15).rankingOptIn(true)
                .rankingType(RankingType.MONTHLY)
                .build();

        when(getRankingPositionUseCase.execute(any(), eq(RankingType.MONTHLY))).thenReturn(pos);

        mockMvc.perform(get("/api/v1/gamificacion/ranking/mi-posicion?tipo=mensual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value(1));
    }
}
