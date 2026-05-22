package com.charizad.compiled.gamification_service.domain.model;

/**
 * Utilidad de dominio para calcular el nivel de un usuario según su XP total (PTR13.2).
 */
public final class NivelCalculator {

    private NivelCalculator() {}

    // Umbrales sugeridos de XP para los niveles 1 a 5
    // Nivel 1: 0 - 499 XP
    // Nivel 2: 500 - 1499 XP
    // Nivel 3: 1500 - 2999 XP
    // Nivel 4: 3000 - 4999 XP
    // Nivel 5: 5000+ XP
    private static final int[] XP_THRESHOLDS = {0, 500, 1500, 3000, 5000};
    
    private static final String[] LEVEL_NAMES = {
            "Novato",
            "Explorador",
            "Conector",
            "Embajador",
            "Leyenda"
    };

    public static int getNivel(int totalXp) {
        for (int i = XP_THRESHOLDS.length - 1; i >= 0; i--) {
            if (totalXp >= XP_THRESHOLDS[i]) {
                return i + 1;
            }
        }
        return 1;
    }

    public static String getNivelName(int nivel) {
        if (nivel < 1) return LEVEL_NAMES[0];
        if (nivel > LEVEL_NAMES.length) return LEVEL_NAMES[LEVEL_NAMES.length - 1];
        return LEVEL_NAMES[nivel - 1];
    }

    public static int getXpParaSiguienteNivel(int totalXp) {
        int nivel = getNivel(totalXp);
        if (nivel >= XP_THRESHOLDS.length) {
            return 0;
        }
        return XP_THRESHOLDS[nivel];
    }

    public static int getXpRestante(int totalXp) {
        int nextLevelXp = getXpParaSiguienteNivel(totalXp);
        if (nextLevelXp == 0) return 0;
        return nextLevelXp - totalXp;
    }
}
