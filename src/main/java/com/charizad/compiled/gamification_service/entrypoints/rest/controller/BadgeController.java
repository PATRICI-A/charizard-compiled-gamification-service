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
@Tag(name = "Badges (Monas)", description = "Management of the collectible badge (mona) catalog — RF13.1")
@SecurityRequirement(name = "bearerAuth")
public class BadgeController {

    private final CreateBadgeUseCase createBadgeUseCase;
    private final AwardBadgeUseCase awardBadgeUseCase;

    @PostMapping
    @Operation(summary = "Create badge", description = "Registers a new badge in the catalog. Only accessible by ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Badge created successfully",
                    content = @Content(schema = @Schema(implementation = BadgeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<BadgeResponse> createBadge(@Valid @RequestBody CreateBadgeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createBadgeUseCase.execute(request));
    }

    @PostMapping("/award")
    @Operation(summary = "Award badge to a user",
            description = "Assigns an existing badge to a user. If the user already has it, the badge is returned without error. Only accessible by ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Badge awarded (or already owned)",
                    content = @Content(schema = @Schema(implementation = EarnedBadgeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "Badge or user not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<EarnedBadgeResponse> awardBadge(@Valid @RequestBody AwardBadgeRequest request) {
        return ResponseEntity.ok(awardBadgeUseCase.execute(request));
    }
}
