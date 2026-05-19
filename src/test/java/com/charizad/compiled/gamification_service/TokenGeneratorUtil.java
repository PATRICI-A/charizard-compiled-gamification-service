package com.charizad.compiled.gamification_service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Util de desarrollo — genera tokens JWT válidos para probar en Swagger local.
 * Eliminar antes de ir a producción.
 */
public class TokenGeneratorUtil {

    private static final String SECRET = "P4tric1A-Auth-S3rv1c3-S3cr3t-K3y-2026!!";

    public static void main(String[] args) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        String adminToken = Jwts.builder()
                .subject("admin-001")
                .claims(Map.of("role", "ADMIN"))
                .signWith(key)
                .compact();

        String userToken = Jwts.builder()
                .subject("estudiante-001")
                .claims(Map.of("role", "USER"))
                .signWith(key)
                .compact();

        System.out.println("=== TOKENS PARA SWAGGER ===");
        System.out.println("ADMIN : " + adminToken);
        System.out.println("USER  : " + userToken);
    }
}
