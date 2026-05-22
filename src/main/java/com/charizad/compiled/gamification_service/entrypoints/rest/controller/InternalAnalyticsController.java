package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserLevelResponse;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserBadgesUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserLevelUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Internal endpoints consumed by AnalyticsService (PTR17) via inter-service Feign calls.
 * No JWT required — secured at network level via Kong (calls originate from within the cluster).
 */
@RestController
@RequestMapping("/api/v1/gamificacion/internal")
@RequiredArgsConstructor
@Tag(name = "Internal - Analytics", description = "Internal endpoints for AnalyticsService inter-service calls (no JWT required)")
public class InternalAnalyticsController {

    private final GetUserBadgesUseCase getUserBadgesUseCase;
    private final GetUserLevelUseCase getUserLevelUseCase;

    /**
     * Returns all earned badges for a given user.
     * Called by AnalyticsService to populate the achievements section of the student dashboard.
     *
     * @param userId the user's ID as a plain string (UUID format)
     * @return list of earned badge responses
     */
    @GetMapping("/user/{userId}/achievements")
    @Operation(summary = "Get earned badges for a user (inter-service, no JWT)")
    @ApiResponse(responseCode = "200", description = "List of earned badges")
    public ResponseEntity<List<EarnedBadgeResponse>> getUserAchievements(
            @PathVariable String userId) {
        return ResponseEntity.ok(getUserBadgesUseCase.execute(userId));
    }

    /**
     * Returns the current level data for a given user.
     * Called by AnalyticsService to populate progressToNextLevel in the student dashboard.
     *
     * @param userId the user's ID as a plain string (UUID format)
     * @return level response with totalMonas and monasParaSiguienteNivel
     */
    @GetMapping("/user/{userId}/level")
    @Operation(summary = "Get level data for a user (inter-service, no JWT)")
    @ApiResponse(responseCode = "200", description = "User level data")
    public ResponseEntity<UserLevelResponse> getUserLevel(
            @PathVariable String userId) {
        return ResponseEntity.ok(getUserLevelUseCase.execute(userId));
    }
}
