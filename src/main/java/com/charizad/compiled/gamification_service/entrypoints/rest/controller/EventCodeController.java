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
@Tag(name = "Event Codes (Admin)", description = "Manage alphanumeric attendance codes for university events. Admins generate these codes and distribute them at events. Students redeem them via the Gamification endpoint to earn the 'Asistente' badge.")
@SecurityRequirement(name = "bearerAuth")
public class EventCodeController {

    private final EventCodeRepositoryPort eventCodeRepository;

    @PostMapping
    @Operation(
            summary = "Create a new event attendance code",
            description = "Registers a new alphanumeric code that can be distributed to students attending a university event. " +
                          "When a student redeems this code through the POST /api/v1/gamificacion/monas/evento endpoint, " +
                          "they earn the 'Asistente' (event attendance) badge along with its associated XP. " +
                          "Each code has a defined validity window (validFrom / validUntil). " +
                          "Codes submitted outside that window will be rejected at redemption time. " +
                          "The code value must be unique across the system — submitting a duplicate code returns a 409 conflict. " +
                          "This endpoint is restricted to users with the ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event code created successfully. Returns the full code record including its generated ID, the code value, and the validity window.",
                    content = @Content(schema = @Schema(implementation = EventCodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body. One or more fields are missing, blank, or have invalid values (e.g. validUntil is before validFrom).", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied. This endpoint requires the ADMIN role. Regular USER accounts cannot create event codes.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict. An event code with the same alphanumeric value already exists in the system.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while persisting the event code.", content = @Content)
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
