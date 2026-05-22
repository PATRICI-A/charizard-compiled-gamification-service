package com.charizad.compiled.gamification_service.entrypoints.rest.mapper;

import com.charizad.compiled.gamification_service.application.dto.request.AwardMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.request.CreateMonaRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MonaRestMapperTest {

    private final MonaRestMapper mapper = new MonaRestMapper();

    @Test
    @DisplayName("toCreateRequest retorna el mismo objeto recibido")
    void toCreateRequest_shouldReturnSameObject() {
        CreateMonaRequest request = CreateMonaRequest.builder()
                .name("Test")
                .description("Desc")
                .build();

        CreateMonaRequest result = mapper.toCreateRequest(request);

        assertThat(result).isSameAs(request);
        assertThat(result.getName()).isEqualTo("Test");
    }

    @Test
    @DisplayName("toAwardRequest construye AwardMonaRequest correctamente")
    void toAwardRequest_shouldBuildRequest() {
        AwardMonaRequest result = mapper.toAwardRequest("user-001", "Mona-001");

        assertThat(result.getUserId()).isEqualTo("user-001");
        assertThat(result.getMonaId()).isEqualTo("Mona-001");
    }
}
