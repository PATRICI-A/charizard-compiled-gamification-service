package com.charizad.compiled.gamification_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Payload enviado por el geo service vía Feign cuando el usuario actualiza su ubicación
 * exitosamente dentro del campus. Permite evaluar las monas Explorador I y II (RF13.1).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneVisitedRequest {

    @NotBlank(message = "campusZone es obligatorio")
    private String campusZone;
}
