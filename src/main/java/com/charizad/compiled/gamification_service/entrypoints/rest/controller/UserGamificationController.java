package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.response.BadgeProgressResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedRewardResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserBadgesUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserProgressUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserRewardsUseCase;
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
@Tag(name = "Gamification", description = "Query badges, XP, progress and ranking")
@SecurityRequirement(name = "bearerAuth")
public class UserGamificationController {

    private final GetUserBadgesUseCase getUserBadgesUseCase;
    private final GetUserProgressUseCase getUserProgressUseCase;
    private final GetUserStatsUseCase getUserStatsUseCase;
    private final ToggleRankingOptInUseCase toggleRankingOptInUseCase;
    private final GetRankingUseCase getRankingUseCase;
    private final GetUserRewardsUseCase getUserRewardsUseCase;

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

    @PatchMapping("/me/ranking/toggle")
    @Operation(summary = "Join / leave weekly ranking",
            description = "Toggles the user's participation in the weekly ranking. If they were participating, they leave; if not, they join.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
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
    @Operation(summary = "Weekly ranking", description = "Returns the top N users participating in the ranking, sorted by weekly XP from highest to lowest.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Weekly ranking",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RankingEntryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<List<RankingEntryResponse>> getRanking(
            @Parameter(description = "Maximum number of positions to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getRankingUseCase.execute(limit));
    }
}
