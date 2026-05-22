package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.CreateRewardRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedRewardResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RewardResponse;
import com.charizad.compiled.gamification_service.domain.ports.in.CreateRewardUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserRewardsUseCase;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "Management of XP-threshold rewards and user reward queries")
@SecurityRequirement(name = "bearerAuth")
public class RewardController {

    private final CreateRewardUseCase createRewardUseCase;
    private final GetUserRewardsUseCase getUserRewardsUseCase;

    // ─── Admin endpoints ──────────────────────────────────────────────────────

    @PostMapping
    @Operation(
            summary = "Create reward (ADMIN)",
            description = "Registers a new XP-threshold reward in the catalogue. Only accessible by ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reward created successfully",
                    content = @Content(schema = @Schema(implementation = RewardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<RewardResponse> createReward(@Valid @RequestBody CreateRewardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createRewardUseCase.execute(request));
    }

    // ─── User endpoints ───────────────────────────────────────────────────────

    @GetMapping("/me")
    @Operation(
            summary = "My unlocked rewards",
            description = "Returns all rewards that the authenticated user has unlocked by reaching their XP threshold."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of unlocked rewards",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EarnedRewardResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<List<EarnedRewardResponse>> getMyRewards(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId) {
        return ResponseEntity.ok(getUserRewardsUseCase.execute(userId.toString()));
    }
}
