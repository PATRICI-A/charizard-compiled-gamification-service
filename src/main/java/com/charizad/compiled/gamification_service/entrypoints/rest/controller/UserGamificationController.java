package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.RedeemEventCodeRequest;
import com.charizad.compiled.gamification_service.application.dto.request.SetRankingOptInRequest;
import com.charizad.compiled.gamification_service.application.dto.response.BadgeProgressResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedRewardResponse;
import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserLevelResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingPositionUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserLevelUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserBadgesUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserProgressUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserRewardsUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserStatsUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetMonaByIdUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetMonasUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.RedeemEventCodeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.ToggleRankingOptInUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/gamificacion")
@RequiredArgsConstructor
@Tag(name = "Gamification", description = "Monas, niveles, estadísticas y ranking semanal — RF13.1/13.2/13.3")
@SecurityRequirement(name = "bearerAuth")
public class UserGamificationController {

    private final GetUserBadgesUseCase getUserBadgesUseCase;
    private final GetUserProgressUseCase getUserProgressUseCase;
    private final GetUserStatsUseCase getUserStatsUseCase;
    private final ToggleRankingOptInUseCase toggleRankingOptInUseCase;
    private final GetRankingUseCase getRankingUseCase;
    private final GetUserRewardsUseCase getUserRewardsUseCase;
    private final GetUserLevelUseCase getUserLevelUseCase;
    private final GetRankingPositionUseCase getRankingPositionUseCase;
    private final RedeemEventCodeUseCase redeemEventCodeUseCase;
    private final GetMonasUseCase getMonasUseCase;
    private final GetMonaByIdUseCase getMonaByIdUseCase;

    @PostMapping("/monas/evento")
    @Operation(summary = "Canjear código de evento",
            description = "El estudiante ingresa el código alfanumérico de un evento universitario para desbloquear la mona 'Asistente' (RF13.1 — Flujo B).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mona 'Asistente' desbloqueada",
                    content = @Content(schema = @Schema(implementation = EarnedBadgeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Código no válido o ya utilizado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente", content = @Content)
    })
    public ResponseEntity<EarnedBadgeResponse> redeemEventCode(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId,
            @Valid @RequestBody RedeemEventCodeRequest request) {
        return ResponseEntity.ok(redeemEventCodeUseCase.execute(userId, request.getEventCode()));
    }

    @GetMapping("/monas")
    @Operation(summary = "Listado de monas",
            description = "Retorna todas las monas del catálogo con estado, progreso y fecha de obtención del estudiante autenticado (RF13.1 — Flujo C).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de monas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MonaResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente", content = @Content)
    })
    public ResponseEntity<List<MonaResponse>> getMonas(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getMonasUseCase.execute(userId));
    }

    @GetMapping("/monas/{monaId}")
    @Operation(summary = "Detalle de mona",
            description = "Retorna el detalle de una mona específica y el progreso del estudiante hacia ella (RF13.1 — Flujo C).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle de la mona",
                    content = @Content(schema = @Schema(implementation = MonaResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Mona no encontrada", content = @Content)
    })
    public ResponseEntity<MonaResponse> getMonaById(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId,
            @PathVariable String monaId) {
        return ResponseEntity.ok(getMonaByIdUseCase.execute(userId, monaId));
    }

    @GetMapping("/me/badges")
    @Operation(summary = "My unlocked badges", description = "Returns all badges that the authenticated user has earned.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of earned badges",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EarnedBadgeResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<List<EarnedBadgeResponse>> getMyBadges(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserBadgesUseCase.execute(userId));
    }

    @GetMapping("/me/progress")
    @Operation(summary = "My progress towards badges", description = "Returns the current user progress towards each not yet unlocked badge.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress list per badge",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BadgeProgressResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<List<BadgeProgressResponse>> getMyProgress(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserProgressUseCase.execute(userId));
    }

    @GetMapping("/me/stats")
    @Operation(summary = "My gamification stats", description = "Returns total XP, weekly XP, number of earned badges and ranking participation status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User statistics",
                    content = @Content(schema = @Schema(implementation = UserStatsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<UserStatsResponse> getMyStats(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserStatsUseCase.execute(userId));
    }

    @PatchMapping("/me/ranking/optin")
    @Operation(summary = "Set weekly ranking participation",
            description = "Explicitly sets the user's participation in the weekly ranking (RF13.3). " +
                          "Send {\"participar\": true} to join, {\"participar\": false} to leave.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Missing or invalid body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> setRankingOptIn(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody SetRankingOptInRequest request) {
        boolean optIn = toggleRankingOptInUseCase.execute(userId, request.getParticipar());
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "rankingOptIn", optIn,
                "message", optIn ? "Ahora participas en el ranking semanal." : "Has salido del ranking semanal."
        ));
    }

    @GetMapping("/me/rewards")
    @Operation(summary = "My unlocked rewards", description = "Returns all XP-threshold rewards that the authenticated user has unlocked.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of unlocked rewards",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EarnedRewardResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<List<EarnedRewardResponse>> getMyRewards(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserRewardsUseCase.execute(userId));
    }

    @GetMapping("/ranking")
    @Operation(summary = "Ranking leaderboard",
            description = "Returns all opted-in users sorted by period monas descending. " +
                          "Supports WEEKLY (default), MONTHLY and SEMESTER ranking types (RF13.3 / RN-13.3.2).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking leaderboard",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RankingEntryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<List<RankingEntryResponse>> getRanking(
            @Parameter(description = "Ranking period type: WEEKLY, MONTHLY or SEMESTER", example = "WEEKLY")
            @RequestParam(defaultValue = "WEEKLY") RankingType type) {
        return ResponseEntity.ok(getRankingUseCase.execute(type));
    }

    @GetMapping("/ranking/my-position")
    @Operation(summary = "My ranking position",
            description = "Returns the authenticated user's current position in the chosen ranking. " +
                          "Returns position=null and rankingOptIn=false when the user is not opted in (RF13.3).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User's position in the ranking",
                    content = @Content(schema = @Schema(implementation = RankingPositionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<RankingPositionResponse> getMyPosition(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId,
            @Parameter(description = "Ranking period type: WEEKLY, MONTHLY or SEMESTER", example = "WEEKLY")
            @RequestParam(defaultValue = "WEEKLY") RankingType type) {
        return ResponseEntity.ok(getRankingPositionUseCase.execute(userId, type));
    }

    @GetMapping("/me/nivel")
    @Operation(summary = "My current level",
            description = "Returns the authenticated user's level based on total monas collected ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User level information",
                    content = @Content(schema = @Schema(implementation = UserLevelResponse.class))),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<UserLevelResponse> getMiNivel(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserLevelUseCase.execute(userId));
    }
}
