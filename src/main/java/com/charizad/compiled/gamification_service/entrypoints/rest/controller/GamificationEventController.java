package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.BadgeUnlockEventRequest;
import com.charizad.compiled.gamification_service.application.dto.request.ZoneVisitedRequest;
import com.charizad.compiled.gamification_service.domain.model.BadgeUnlockEventType;
import com.charizad.compiled.gamification_service.domain.ports.in.CheckBadgeUnlockUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
import java.util.UUID;

/**
 * Endpoints llamados por otros microservicios vía OpenFeign para reportar
 * acciones del usuario que pueden disparar el desbloqueo de monas (Flujo A — RF13.1).
 */
@RestController
@RequestMapping("/api/v1/gamificacion/events")
@RequiredArgsConstructor
@Tag(name = "Gamification Events (Internal)", description = "Internal service-to-service endpoints called by other microservices to report user platform actions that may trigger automatic badge (mona) unlocks. These endpoints are not intended to be called directly by frontend clients.")
@SecurityRequirement(name = "bearerAuth")
public class GamificationEventController {

    private final CheckBadgeUnlockUseCase checkBadgeUnlockUseCase;

    @PostMapping("/zone-visited")
    @Operation(
            summary = "Report a campus zone visit (internal)",
            description = "Called internally by the Geo Service (via OpenFeign) when an authenticated user successfully updates their location within a recognized campus zone. " +
                          "This endpoint evaluates whether the reported zone visit triggers the unlock of exploration badges: " +
                          "'Explorador I' is awarded after visiting 3 distinct campus zones, and 'Explorador II' after visiting 5 distinct zones. " +
                          "The service records the visited zone in the user's gamification profile and checks accumulated distinct zone count against badge thresholds. " +
                          "If one or more badges are unlocked as a result, the corresponding XP is credited and the badge IDs are returned in the response. " +
                          "If no new badges are unlocked, the response contains an empty list of awarded badge IDs. " +
                          "This endpoint assumes geolocation is enabled for the user (geoLocationEnabled is set to true implicitly). " +
                          "It should not be called directly by frontend clients — use the Geo Service API instead."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Zone visit processed successfully. Returns a map containing the userId, the campusZone reported, and a list of IDs of any badges newly awarded. The awardedBadgeIds list is empty if no new badges were unlocked."),
            @ApiResponse(responseCode = "400", description = "Invalid request body. The campusZone field is missing, blank, or contains an unrecognized zone identifier.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required. The forwarded JWT token is missing, expired, or malformed.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied. The caller does not have the required service-level permissions to invoke this internal endpoint.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found. The user's gamification record has not been initialized in the system.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while processing the zone visit event.", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> onZoneVisited(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId,
            @Valid @RequestBody ZoneVisitedRequest request) {

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId(userId)
                .eventType(BadgeUnlockEventType.ZONE_VISITED)
                .campusZone(request.getCampusZone())
                .geoLocationEnabled(true)
                .build();

        List<UUID> awarded = checkBadgeUnlockUseCase.execute(event);

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "campusZone", request.getCampusZone(),
                "awardedBadgeIds", awarded
        ));
    }
}
