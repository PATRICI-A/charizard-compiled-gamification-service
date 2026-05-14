package com.charizad.compiled.gamification_service.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccessDeniedExceptionTest {

    @Test
    @DisplayName("adminOnly crea excepción con mensaje en español")
    void adminOnly_shouldCreateExceptionWithSpanishMessage() {
        AccessDeniedException exception = AccessDeniedException.adminOnly();

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo("Esta operación requiere rol ADMIN.");
    }

    @Test
    @DisplayName("Constructor con mensaje personalizado")
    void constructor_shouldSetCustomMessage() {
        AccessDeniedException exception = new AccessDeniedException("Mensaje personalizado");

        assertThat(exception.getMessage()).isEqualTo("Mensaje personalizado");
    }
}
