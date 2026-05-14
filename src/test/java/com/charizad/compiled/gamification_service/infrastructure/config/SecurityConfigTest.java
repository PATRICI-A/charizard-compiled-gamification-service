package com.charizad.compiled.gamification_service.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    @DisplayName("restTemplate bean se crea correctamente")
    void restTemplate_shouldCreateBean() {
        RestTemplate restTemplate = securityConfig.restTemplate();

        assertThat(restTemplate).isNotNull();
    }
}
