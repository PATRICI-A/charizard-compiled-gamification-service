package com.charizad.compiled.gamification_service.application.dto.request;

import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBadgeRequest {

    @NotBlank(message = "El nombre de la insignia es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "La categoría es obligatoria")
    private BadgeCategory category;

    @Min(value = 1, message = "La recompensa de XP debe ser mayor que 0")
    private int xpReward;

    private String iconUrl;
}
