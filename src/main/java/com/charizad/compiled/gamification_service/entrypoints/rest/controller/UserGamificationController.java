package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.response.BadgeProgressResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserBadgesUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserProgressUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserStatsUseCase;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/gamification")
@RequiredArgsConstructor
@Tag(name = "Gamificación", description = "Consulta de monas, XP, progreso y ranking")
@SecurityRequirement(name = "bearerAuth")
public class UserGamificationController {

    private final GetUserBadgesUseCase getUserBadgesUseCase;
    private final GetUserProgressUseCase getUserProgressUseCase;
    private final GetUserStatsUseCase getUserStatsUseCase;
    private final ToggleRankingOptInUseCase toggleRankingOptInUseCase;
    private final GetRankingUseCase getRankingUseCase;

    @GetMapping("/me/badges")
    @Operation(summary = "Mis insignias desbloqueadas", description = "Retorna todas las insignias que el usuario autenticado ha ganado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de insignias ganadas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EarnedBadgeResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Perfil de gamificación no encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido", content = @Content)
    })
    public ResponseEntity<List<EarnedBadgeResponse>> getMyBadges(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserBadgesUseCase.execute(userId));
    }

    @GetMapping("/me/progress")
    @Operation(summary = "Mi progreso hacia insignias", description = "Retorna el progreso actual del usuario hacia cada insignia aún no desbloqueada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de progreso por insignia",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BadgeProgressResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Perfil de gamificación no encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido", content = @Content)
    })
    public ResponseEntity<List<BadgeProgressResponse>> getMyProgress(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserProgressUseCase.execute(userId));
    }

    @GetMapping("/me/stats")
    @Operation(summary = "Mis estadísticas de gamificación", description = "Retorna XP total, XP semanal, cantidad de insignias ganadas y estado de participación en el ranking.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estadísticas del usuario",
                    content = @Content(schema = @Schema(implementation = UserStatsResponse.class))),
            @ApiResponse(responseCode = "404", description = "Perfil de gamificación no encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido", content = @Content)
    })
    public ResponseEntity<UserStatsResponse> getMyStats(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserStatsUseCase.execute(userId));
    }

    @PatchMapping("/me/ranking/toggle")
    @Operation(summary = "Entrar / salir del ranking semanal",
            description = "Alterna la participación del usuario en el ranking semanal. Si estaba participando, sale; si no, entra.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> toggleRanking(
            @AuthenticationPrincipal String userId) {
        boolean optIn = toggleRankingOptInUseCase.execute(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "rankingOptIn", optIn,
                "message", optIn ? "Ahora participas en el ranking semanal." : "Has salido del ranking semanal."
        ));
    }

    @GetMapping("/ranking")
    @Operation(summary = "Ranking semanal", description = "Retorna el top N de usuarios que participan en el ranking, ordenados por XP semanal de mayor a menor.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking semanal",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RankingEntryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido", content = @Content)
    })
    public ResponseEntity<List<RankingEntryResponse>> getRanking(
            @Parameter(description = "Número máximo de posiciones a retornar", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getRankingUseCase.execute(limit));
    }
}
