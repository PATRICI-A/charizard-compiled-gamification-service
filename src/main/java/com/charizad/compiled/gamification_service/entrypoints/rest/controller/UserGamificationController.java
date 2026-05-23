package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.RedeemEventCodeRequest;
import com.charizad.compiled.gamification_service.application.dto.request.SetRankingOptInRequest;
import com.charizad.compiled.gamification_service.application.dto.response.MonaProgressResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedRewardResponse;
import com.charizad.compiled.gamification_service.application.dto.response.MonaDetailResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserLevelResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingPositionUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserLevelUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserMonasUseCase;
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

    private final GetUserMonasUseCase getUserMonasUseCase;
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
                    content = @Content(schema = @Schema(implementation = EarnedMonaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Código no válido o ya utilizado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente", content = @Content)
    })
    public ResponseEntity<EarnedMonaResponse> redeemEventCode(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId,
            @Valid @RequestBody RedeemEventCodeRequest request) {
        return ResponseEntity.ok(redeemEventCodeUseCase.execute(userId, request.getEventCode()));
    }

    @GetMapping("/monas")
    @Operation(summary = "Listado de monas",
            description = "Retorna todas las monas del catálogo con estado, progreso y fecha de obtención del estudiante autenticado (RF13.1 — Flujo C).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de monas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MonaDetailResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente", content = @Content)
    })
    public ResponseEntity<List<MonaDetailResponse>> getMonas(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getMonasUseCase.execute(userId));
    }

    @GetMapping("/monas/{monaId}")
    @Operation(summary = "Detalle de mona",
            description = "Retorna el detalle de una mona específica y el progreso del estudiante hacia ella (RF13.1 — Flujo C).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle de la mona",
                    content = @Content(schema = @Schema(implementation = MonaDetailResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Mona no encontrada", content = @Content)
    })
    public ResponseEntity<MonaDetailResponse> getMonaById(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId,
            @PathVariable String monaId) {
        return ResponseEntity.ok(getMonaByIdUseCase.execute(userId, monaId));
    }

    @PatchMapping("/ranking/optin")
    @Operation(summary = "Set ranking participation",
            description = "Explicitly sets the user's participation in the ranking (RF13.3). " +
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
                "studentId", userId,
                "rankingOptIn", optIn,
                "updatedAt", java.time.LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/ranking")
    @Operation(summary = "Ranking Social",
            description = "Retorna los mejores N usuarios ordenados por monas del período (semanal, mensual o semestral) de forma descendente (PTR13.3).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado del ranking",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RankingEntryResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Tipo de ranking no válido", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<List<RankingEntryResponse>> getRanking(
            @Parameter(description = "Tipo de ranking: WEEKLY, MONTHLY o SEMESTER", example = "WEEKLY")
            @RequestParam(defaultValue = "WEEKLY") com.charizad.compiled.gamification_service.domain.model.enums.RankingType tipo,
            @Parameter(description = "Cantidad máxima de posiciones a retornar", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getRankingUseCase.execute(tipo, limit));
    }

    @GetMapping("/ranking/mi-posicion")
    @Operation(summary = "Mi posición en el ranking",
            description = "Retorna la posición actual del estudiante autenticado en el ranking del período seleccionado. " +
                          "Retorna position=null si el usuario no participa (opt-in = false).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Posición del usuario en el ranking",
                    content = @Content(schema = @Schema(implementation = RankingPositionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tipo de ranking no válido", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<RankingPositionResponse> getMiPosicion(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId,
            @Parameter(description = "Tipo de ranking: WEEKLY, MONTHLY o SEMESTER", example = "WEEKLY")
            @RequestParam(defaultValue = "WEEKLY") com.charizad.compiled.gamification_service.domain.model.enums.RankingType tipo) {
        return ResponseEntity.ok(getRankingPositionUseCase.execute(userId, tipo));
    }

    @GetMapping("/nivel")
    @Operation(summary = "My current level",
            description = "Returns the authenticated user's level based on total XP (PTR13.2)")
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

    // --- Legacy / Internal Stats endpoints ---

    @GetMapping("/me/monas")
    public ResponseEntity<List<EarnedMonaResponse>> getMyMonas(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserMonasUseCase.execute(userId));
    }

    @GetMapping("/me/progress")
    public ResponseEntity<List<MonaProgressResponse>> getMyProgress(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserProgressUseCase.execute(userId));
    }

    @GetMapping("/me/stats")
    public ResponseEntity<UserStatsResponse> getMyStats(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserStatsUseCase.execute(userId));
    }

    @GetMapping("/me/rewards")
    public ResponseEntity<List<EarnedRewardResponse>> getMyRewards(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserRewardsUseCase.execute(userId));
    }
}
