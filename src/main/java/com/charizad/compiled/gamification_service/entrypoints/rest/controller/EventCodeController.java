package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.CreateEventCodeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EventCodeResponse;
import com.charizad.compiled.gamification_service.domain.model.EventCode;
import com.charizad.compiled.gamification_service.domain.ports.out.EventCodeRepositoryPort;
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
@RequestMapping("/api/v1/gamificacion/admin/event-codes")
@RequiredArgsConstructor
@Tag(name = "Event Codes (Admin)", description = "Gestión de códigos alfanuméricos de eventos universitarios — RF13.1 Flujo B")
@SecurityRequirement(name = "bearerAuth")
public class EventCodeController {

    private final EventCodeRepositoryPort eventCodeRepository;

    @PostMapping
    @Operation(summary = "Crear código de evento",
            description = "Registra un nuevo código alfanumérico para un evento universitario. Solo ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Código creado",
                    content = @Content(schema = @Schema(implementation = EventCodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Requiere rol ADMIN", content = @Content)
    })
    public ResponseEntity<EventCodeResponse> createEventCode(@Valid @RequestBody CreateEventCodeRequest request) {
        EventCode created = eventCodeRepository.save(
                EventCode.newCode(request.getCode(), request.getValidFrom(), request.getValidUntil())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    private EventCodeResponse toResponse(EventCode code) {
        return EventCodeResponse.builder()
                .id(code.getId())
                .code(code.getCode())
                .validFrom(code.getValidFrom())
                .validUntil(code.getValidUntil())
                .timesUsed(code.getUsedByUserIds().size())
                .build();
    }
}
