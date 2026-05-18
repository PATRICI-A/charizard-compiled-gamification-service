package com.charizad.compiled.gamification_service.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gamification Service API")
                        .description("""
                                **Module M04 — Gamification**

                                Badge management, XP and weekly ranking.

                                **Authentication:** Bearer JWT issued by M01 (snorlax-energy-auth-service).

                                **Roles:**
                                - `ADMIN` — can create and award badges
                                - `USER` — can query their badges, progress, stats and ranking
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Charizard Compiled")
                                .email("charizard.compiled@universidad.edu.co")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter the JWT token without the 'Bearer ' prefix")));
    }
}
