package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.request.CreateBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.BadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardBadgeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.CreateBadgeUseCase;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gamificacion/badges")
@RequiredArgsConstructor
@Tag(name = "Badges (Monas)", description = "Manage the collectible badge (mona) catalog. Admins can define new badges and manually assign them to users. Badges represent achievements earned through campus exploration, event attendance, and platform activity.")
@SecurityRequirement(name = "bearerAuth")
public class BadgeController {

    private final CreateBadgeUseCase createBadgeUseCase;
    private final AwardBadgeUseCase awardBadgeUseCase;

    @PostMapping
    @Operation(
            summary = "Create a new badge",
            description = "Registers a new collectible badge (mona) in the system catalog. " +
                          "Each badge represents a specific achievement that students can unlock through defined actions " +
                          "such as visiting campus zones, attending events, or completing profile milestones. " +
                          "The badge definition includes its name, description, icon, XP value, and the unlock criteria. " +
                          "Once created, the badge becomes part of the catalog visible to all users in their mona collection. " +
                          "This endpoint is restricted to users with the ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Badge created successfully. Returns the full badge definition including its generated ID.",
                    content = @Content(schema = @Schema(implementation = BadgeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body. One or more required fields are missing, empty, or fail validation constraints (e.g. blank name, negative XP value).", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied. This endpoint requires the ADMIN role. Regular USER accounts cannot create badges.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict. A badge with the same identifier or name already exists in the catalog.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while persisting the badge. Retry the request or contact support.", content = @Content)
    })
    public ResponseEntity<BadgeResponse> createBadge(@Valid @RequestBody CreateBadgeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createBadgeUseCase.execute(request));
    }

    @PostMapping("/award")
    @Operation(
            summary = "Manually award a badge to a user",
            description = "Assigns an existing badge from the catalog to a specific user, bypassing the automatic unlock criteria. " +
                          "This is used by administrators to grant badges for exceptional cases, promotions, or corrections. " +
                          "If the target user already owns the badge, the operation is idempotent — it returns the existing earned badge " +
                          "record without creating a duplicate or throwing an error. " +
                          "The operation updates the user's gamification profile with the new badge and its associated XP reward. " +
                          "Both the badge ID and the target user ID must correspond to existing records in the system. " +
                          "Restricted to ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Badge awarded successfully, or the user already owned it. Returns the earned badge record with unlock timestamp and XP granted.",
                    content = @Content(schema = @Schema(implementation = EarnedBadgeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body. The badge ID or user ID is missing or improperly formatted.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied. This endpoint requires the ADMIN role. Regular USER accounts cannot manually award badges.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Resource not found. The specified badge ID or user ID does not exist in the system.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while awarding the badge. Retry the request or contact support.", content = @Content)
    })
    public ResponseEntity<EarnedBadgeResponse> awardBadge(@Valid @RequestBody AwardBadgeRequest request) {
        return ResponseEntity.ok(awardBadgeUseCase.execute(request));
    }
}
