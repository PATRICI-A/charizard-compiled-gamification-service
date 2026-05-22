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
                                ## Gamification Service — Module M04

                                This microservice is responsible for managing the university platform's gamification system.\s
                                It handles badge (mona) creation and assignment, XP accumulation, level progression,\s
                                reward unlocking, and competitive ranking among students.

                                ### Core Capabilities
                                - **Badge Management:** Define collectible badges (monas) tied to specific user actions\s
                                  such as campus exploration, event attendance, and platform engagement.
                                - **XP & Levels:** Track each student's experience points and compute their current level\s
                                  based on total monas collected.
                                - **Rewards:** Automatically unlock rewards when a student's XP crosses defined thresholds.
                                - **Ranking:** Maintain WEEKLY, MONTHLY, and SEMESTER leaderboards for opted-in students.
                                - **Event Codes:** Allow admins to generate alphanumeric codes that students redeem\s
                                  at university events to earn the "Asistente" badge.

                                ### Badge Unlock Flows
                                - **Flow A — Automatic:** Triggered internally when another microservice (e.g. geo service)\s
                                  reports a qualifying user action (campus zone visit, profile completion, etc.).
                                - **Flow B — Event Code:** Student manually enters a code distributed at a university event.
                                - **Flow C — Catalogue Query:** Students browse their badge collection and progress.

                                ### Authentication
                                All endpoints require a valid **Bearer JWT** token issued by the Auth Service (M01 — snorlax-energy-auth-service).\s
                                Include it in the `Authorization` header as: `Bearer <token>`

                                ### Role-Based Access
                                | Role | Permissions |
                                |------|-------------|
                                | `ADMIN` | Create badges, award badges manually, create event codes, create rewards |
                                | `USER` | Query own badges, progress, stats, level, rewards, ranking |
                                | `SERVICE` | Internal endpoints called by other microservices (zone-visited, etc.) |
                                """)
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
