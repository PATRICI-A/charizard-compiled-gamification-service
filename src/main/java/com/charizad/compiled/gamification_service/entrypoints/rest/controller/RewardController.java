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

@RestController
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
@Tag(name = "Rewards", description = "Manage XP-threshold rewards and query personal reward history. Admins define rewards that are automatically granted when a student's total XP crosses a configured threshold. Students can view all rewards they have unlocked.")
@SecurityRequirement(name = "bearerAuth")
public class RewardController {

    private final CreateRewardUseCase createRewardUseCase;
    private final GetUserRewardsUseCase getUserRewardsUseCase;

    // ─── Admin endpoints ──────────────────────────────────────────────────────

    @PostMapping
    @Operation(
            summary = "Create a new XP-threshold reward",
            description = "Registers a new reward in the system catalogue. A reward is automatically granted to any student " +
                          "whose total accumulated XP reaches or exceeds the defined threshold at the time of any XP-earning event. " +
                          "The reward definition includes a name, description, and the minimum XP threshold required to unlock it. " +
                          "Once created, the reward is evaluated retroactively on subsequent XP events — " +
                          "existing users who already meet the threshold will receive the reward the next time their XP is updated. " +
                          "Each threshold value must be unique across the reward catalogue. " +
                          "This endpoint is restricted to users with the ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reward created successfully. Returns the full reward definition including its generated ID and the XP threshold.",
                    content = @Content(schema = @Schema(implementation = RewardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body. One or more required fields are missing, blank, or fail validation (e.g. non-positive XP threshold).", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied. This endpoint requires the ADMIN role. Regular USER accounts cannot create rewards.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict. A reward with the same XP threshold already exists in the catalogue.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while persisting the reward definition.", content = @Content)
    })
    public ResponseEntity<RewardResponse> createReward(@Valid @RequestBody CreateRewardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createRewardUseCase.execute(request));
    }

    // ─── User endpoints ───────────────────────────────────────────────────────

    @GetMapping("/me")
    @Operation(
            summary = "Get my unlocked rewards",
            description = "Returns all rewards that the authenticated user has unlocked by accumulating enough total XP. " +
                          "Rewards are granted automatically when the user's XP crosses the threshold defined for each reward. " +
                          "Each entry includes the reward name, description, the XP threshold that triggered the unlock, and the date it was granted. " +
                          "Returns an empty array if the user has not yet accumulated enough XP to unlock any reward. " +
                          "Note: this endpoint returns the same data as GET /api/v1/gamificacion/me/rewards and is provided as a convenience alias under the /rewards base path."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of unlocked rewards returned successfully. Each entry contains the reward details and the date it was unlocked. Returns an empty array if no rewards have been earned yet.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EarnedRewardResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found. The user's gamification record has not been created yet.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while retrieving the user's rewards.", content = @Content)
    })
    public ResponseEntity<List<EarnedRewardResponse>> getMyRewards(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserRewardsUseCase.execute(userId));
    }
}
