package com.charizad.compiled.gamification_service.domain.model;

/**
 * Utilidad de dominio para calcular el nivel de un usuario según sus monas totales (RF13.2).
 *
 * Niveles (umbrales — nombres pendientes de definición en los requisitos):
 *   1 →  0–2 monas
 *   2 →  3–5 monas
 *   3 →  6–9 monas
 *   4 → 10–12 monas
 *   5 → 13 monas
 */
public final class NivelCalculator {

    private NivelCalculator() {}

    private static final int[] THRESHOLDS = {0, 3, 6, 10, 13};

    public static int getNivel(int totalMonas) {
        for (int i = THRESHOLDS.length - 1; i >= 0; i--) {
            if (totalMonas >= THRESHOLDS[i]) {
                return i + 1;
            }
        }
        return 1;
    }

    /**
     * Nombre del nivel. Pendiente de definición en RF13.2.
     * Retorna null hasta que los nombres sean confirmados en los requisitos.
     */
    public static String getNivelName(int nivel) {
        return null;
    }

    public static String getNivelNameByMonas(int totalMonas) {
        return getNivelName(getNivel(totalMonas));
    }

    /**
     * Monas que faltan para alcanzar el siguiente nivel.
     * Devuelve 0 si el usuario ya está en el nivel máximo (5).
     */
    public static int getMonasParaSiguienteNivel(int totalMonas) {
        int nivel = getNivel(totalMonas);
        if (nivel >= THRESHOLDS.length) {
            return 0;
        }
        return THRESHOLDS[nivel] - totalMonas;
    }
}
