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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
@Tag(name = "Gamification Events", description = "Endpoints para microservicios — reportan acciones de plataforma que evalúan desbloqueo de monas (Flujo A, RF13.1)")
@SecurityRequirement(name = "bearerAuth")
public class GamificationEventController {

    private final CheckBadgeUnlockUseCase checkBadgeUnlockUseCase;

    /**
     * Llamado por el geo service vía Feign cuando el usuario actualiza su ubicación
     * exitosamente dentro del campus (RN-13.1.5: geoLocationEnabled=true implícito).
     * Evalúa monas: Explorador I (3 zonas distintas), Explorador II (5 zonas distintas).
     */
    @PostMapping("/zone-visited")
    @Operation(
            summary = "Registrar visita a zona del campus",
            description = "Llamado por el geo service cuando el usuario actualiza su ubicación en el campus. " +
                          "Evalúa y otorga las monas Explorador I y Explorador II según las zonas acumuladas (RF13.1 — Flujo A).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación completada. Lista de IDs de monas otorgadas (puede ser vacía)."),
            @ApiResponse(responseCode = "400", description = "campusZone inválido o ausente", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> onZoneVisited(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID principalUserId,
            @Valid @RequestBody ZoneVisitedRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated();
        String bodyUserId = request.getUserId() != null ? request.getUserId().toString() : null;
        String userId = isAuthenticated ? principalUserId.toString() : bodyUserId;

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "userId requerido: envíalo en el body o vía JWT"));
        }

        BadgeUnlockEventRequest event = BadgeUnlockEventRequest.builder()
                .userId(userId)
                .eventType(BadgeUnlockEventType.ZONE_VISITED)
                .campusZone(request.getCampusZone())
                .geoLocationEnabled(true)
                .build();

        List<String> awarded = checkBadgeUnlockUseCase.execute(event);

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "campusZone", request.getCampusZone(),
                "awardedBadgeIds", awarded
        ));
    }
}
