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
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
@Tag(name = "Badges", description = "Gestión del catálogo de insignias (monas)")
@SecurityRequirement(name = "bearerAuth")
public class BadgeController {

    private final CreateBadgeUseCase createBadgeUseCase;
    private final AwardBadgeUseCase awardBadgeUseCase;

    @PostMapping
    @Operation(summary = "Crear insignia", description = "Registra una nueva insignia en el catálogo. Solo accesible por ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Insignia creada exitosamente",
                    content = @Content(schema = @Schema(implementation = BadgeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido", content = @Content)
    })
    public ResponseEntity<BadgeResponse> createBadge(@Valid @RequestBody CreateBadgeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createBadgeUseCase.execute(request));
    }

    @PostMapping("/award")
    @Operation(summary = "Otorgar insignia a un usuario",
            description = "Asigna una insignia existente a un usuario. Si el usuario ya la posee, se retorna la insignia sin error. Solo accesible por ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Insignia otorgada (o ya poseída)",
                    content = @Content(schema = @Schema(implementation = EarnedBadgeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere rol ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Insignia o usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido", content = @Content)
    })
    public ResponseEntity<EarnedBadgeResponse> awardBadge(@Valid @RequestBody AwardBadgeRequest request) {
        return ResponseEntity.ok(awardBadgeUseCase.execute(request));
    }
}
