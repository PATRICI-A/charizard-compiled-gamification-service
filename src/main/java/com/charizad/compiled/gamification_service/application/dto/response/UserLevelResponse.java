package com.charizad.compiled.gamification_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLevelResponse {
    private String userId;
    private int nivel;
    private String levelName;
    private int totalMonas;
    private int totalXp;
    private int xpParaSiguienteNivel;
    private int xpRestante;
}
