package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.AwardMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.request.CreateMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardMonaUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.CreateMonaUseCase;
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
@RequestMapping("/api/v1/gamificacion/Monas")
@RequiredArgsConstructor
@Tag(name = "Monas (Monas)", description = "Management of the collectible Mona (mona) catalog — RF13.1")
@SecurityRequirement(name = "bearerAuth")
public class MonaController {

    private final CreateMonaUseCase createMonaUseCase;
    private final AwardMonaUseCase awardMonaUseCase;

    @PostMapping
    @Operation(summary = "Create Mona", description = "Registers a new Mona in the catalog. Only accessible by ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mona created successfully",
                    content = @Content(schema = @Schema(implementation = MonaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<MonaResponse> createMona(@Valid @RequestBody CreateMonaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createMonaUseCase.execute(request));
    }

    @PostMapping("/award")
    @Operation(summary = "Award Mona to a user",
            description = "Assigns an existing Mona to a user. If the user already has it, the Mona is returned without error. Only accessible by ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mona awarded (or already owned)",
                    content = @Content(schema = @Schema(implementation = EarnedMonaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "Mona or user not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content)
    })
    public ResponseEntity<EarnedMonaResponse> awardMona(@Valid @RequestBody AwardMonaRequest request) {
        return ResponseEntity.ok(awardMonaUseCase.execute(request));
    }
}
