package com.charizad.compiled.gamification_service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TokenGeneratorUtil {

    private static final String SECRET = "P4tric1A-Auth-S3rv1c3-S3cr3t-K3y-2026!!";

    public static void main(String[] args) {
        String adminToken = generateToken("admin-001", "ADMIN");
        String userToken = generateToken("estudiante-001", "USER");

        System.out.println("=== TOKENS PARA SWAGGER ===");
        System.out.println("ADMIN : " + adminToken);
        System.out.println("USER  : " + userToken);
    }

    public static String generateToken(String subject, String role) {
        String header = base64("{\"alg\":\"HS256\"}");
        String payload = base64("{\"sub\":\"" + subject + "\",\"role\":\"" + role + "\"}");
        return header + "." + payload + "." + base64(SECRET);
    }

    private static String base64(String input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}
