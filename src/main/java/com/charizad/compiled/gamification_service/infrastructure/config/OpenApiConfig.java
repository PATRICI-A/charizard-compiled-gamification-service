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
                        .description(
                                "This microservice is responsible for managing the university platform's gamification system. " +
                                "It handles badge (mona) creation and assignment, XP accumulation, level progression, " +
                                "reward unlocking, and competitive ranking among students. " +
                                "Badge unlocks are triggered automatically when another microservice reports a qualifying user action " +
                                "(such as a campus zone visit or profile completion), when a student manually redeems an attendance code " +
                                "distributed at a university event, or when a student browses their badge collection and progress. " +
                                "Rewards are granted automatically when a student's total XP crosses a defined threshold. " +
                                "Rankings are maintained on weekly, monthly, and semester periods for opted-in students. " +
                                "All endpoints require a valid Bearer JWT token issued by the Auth Service (M01 — snorlax-energy-auth-service). " +
                                "Admin-restricted operations require the ADMIN role; internal service-to-service calls require the SERVICE role.")
                        .version("v1.0.0")
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
