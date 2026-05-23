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

    private static final String[] REWARDS = {
            null, // Nivel 1 no tiene recompensa según PTR13.2
            "Insignia de Plata en Perfil",
            "Marco Dorado para Avatar",
            "Acceso Prioritario a Eventos",
            "Título de 'Leyenda del Campus'"
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

    public static String getReward(int nivel) {
        if (nivel < 1) return null;
        if (nivel > REWARDS.length) return REWARDS[REWARDS.length - 1];
        return REWARDS[nivel - 1];
    }

    public static Integer getXpParaSiguienteNivel(int totalXp) {
        int nivel = getNivel(totalXp);
        if (nivel >= XP_THRESHOLDS.length) {
            return null;
        }
        return XP_THRESHOLDS[nivel];
    }

    public static Integer getXpRestante(int totalXp) {
        Integer nextLevelXp = getXpParaSiguienteNivel(totalXp);
        if (nextLevelXp == null) return null;
        return nextLevelXp - totalXp;
    }

    public static float getProgressPercentage(int totalXp) {
        int nivel = getNivel(totalXp);
        if (nivel >= XP_THRESHOLDS.length) {
            return 100.0f;
        }
        int currentLevelXp = XP_THRESHOLDS[nivel - 1];
        int nextLevelXp = XP_THRESHOLDS[nivel];
        
        float progress = (float) (totalXp - currentLevelXp) / (nextLevelXp - currentLevelXp) * 100;
        return Math.max(0.0f, Math.min(100.0f, progress));
    }

    public static boolean isMaxLevel(int totalXp) {
        return getNivel(totalXp) >= XP_THRESHOLDS.length;
    }
}
