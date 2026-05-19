package com.charizad.compiled.gamification_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventCodeRequest {

    @NotBlank(message = "El código es obligatorio.")
    private String code;

    @NotNull(message = "La fecha de inicio de validez es obligatoria.")
    private LocalDateTime validFrom;

    @NotNull(message = "La fecha de fin de validez es obligatoria.")
    private LocalDateTime validUntil;
}
