package com.charizad.compiled.gamification_service.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KongAuthFilterTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    private KongAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new KongAuthFilter();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Filtra request sin Authorization header y continúa la cadena")
    void doFilter_shouldContinueChain_whenNoAuthHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Filtra request con header sin Bearer y continúa la cadena")
    void doFilter_shouldContinueChain_whenNotBearerToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic xxx");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Maneja token inválido sin lanzar excepción")
    void doFilter_shouldHandleInvalidTokenGracefully() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Maneja base64 inválido en el payload")
    void doFilter_shouldHandleInvalidBase64() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer header.!!!not-base64!!.signature");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Establece autenticación en SecurityContext con token válido")
    void doFilter_shouldSetAuthentication_whenTokenIsValid() throws Exception {
        String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"HS256\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().encodeToString("{\"sub\":\"user-001\",\"role\":\"ADMIN\"}".getBytes(StandardCharsets.UTF_8));
        String token = header + "." + payload + ".dummysignature";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo("user-001");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Establece rol USER por defecto cuando el token no tiene role")
    void doFilter_shouldDefaultToUserRole() throws Exception {
        String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"HS256\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().encodeToString("{\"sub\":\"user-002\"}".getBytes(StandardCharsets.UTF_8));
        String token = header + "." + payload + ".dummysignature";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    @Test
    @DisplayName("No establece autenticación cuando falta el claim sub")
    void doFilter_shouldNotAuthenticate_whenMissingSub() throws Exception {
        String header = Base64.getUrlEncoder().encodeToString("{\"alg\":\"HS256\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().encodeToString("{\"role\":\"ADMIN\"}".getBytes(StandardCharsets.UTF_8));
        String token = header + "." + payload + ".dummysignature";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
