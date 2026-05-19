package com.charizad.compiled.gamification_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RedeemEventCodeRequest {

    @NotBlank(message = "El código del evento es obligatorio.")
    private String eventCode;
}
