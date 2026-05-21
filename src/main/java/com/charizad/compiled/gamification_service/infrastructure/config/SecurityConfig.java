package com.charizad.compiled.gamification_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, KongAuthFilter kongAuthFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/api-docs",
                    "/v3/api-docs/**",
                    "/v3/api-docs",
                    "/webjars/**",
                    "/actuator/health",
                    "/actuator/health/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/gamificacion/badges").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/gamificacion/badges/award").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/gamificacion/admin/event-codes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/rewards").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(kongAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
