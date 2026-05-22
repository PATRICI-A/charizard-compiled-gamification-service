package com.charizad.compiled.gamification_service.entrypoints.advice;

import com.charizad.compiled.gamification_service.domain.exceptions.AccessDeniedException;
import com.charizad.compiled.gamification_service.domain.exceptions.MonaAlreadyEarnedException;
import com.charizad.compiled.gamification_service.domain.exceptions.MonaNotFoundException;
import com.charizad.compiled.gamification_service.domain.exceptions.UserGamificationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("MonaNotFoundException retorna 404")
    void handleMonaNotFound_shouldReturn404() {
        MonaNotFoundException ex = new MonaNotFoundException("Mona-001");

        ResponseEntity<Map<String, Object>> response = handler.handleMonaNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Insignia no encontrada con id: Mona-001");
    }

    @Test
    @DisplayName("UserGamificationNotFoundException retorna 404")
    void handleUserNotFound_shouldReturn404() {
        UserGamificationNotFoundException ex = new UserGamificationNotFoundException("user-001");

        ResponseEntity<Map<String, Object>> response = handler.handleUserNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isNotNull();
    }

    @Test
    @DisplayName("MonaAlreadyEarnedException retorna 409")
    void handleMonaAlreadyEarned_shouldReturn409() {
        MonaAlreadyEarnedException ex = new MonaAlreadyEarnedException("user-001", "Mona-001");

        ResponseEntity<Map<String, Object>> response = handler.handleMonaAlreadyEarned(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("AccessDeniedException retorna 403")
    void handleAccessDenied_shouldReturn403() {
        AccessDeniedException ex = AccessDeniedException.adminOnly();

        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Esta operación requiere rol ADMIN.");
    }

    @Test
    @DisplayName("Exception genérica retorna 500")
    void handleGeneric_shouldReturn500() {
        Exception ex = new RuntimeException("Algo falló");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Error interno del servidor.");
    }

    @Test
    @DisplayName("buildError incluye timestamp, status, error y message")
    void buildError_shouldIncludeAllFields() {
        MonaNotFoundException ex = new MonaNotFoundException("test");

        ResponseEntity<Map<String, Object>> response = handler.handleMonaNotFound(ex);

        assertThat(response.getBody()).containsKeys("timestamp", "status", "error", "message");
        assertThat(response.getBody().get("status")).isEqualTo(404);
    }
}
