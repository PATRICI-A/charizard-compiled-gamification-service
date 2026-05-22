package com.charizad.compiled.gamification_service.application.mapper;

import com.charizad.compiled.gamification_service.application.dto.request.CreateMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MonaMapperTest {

    private final MonaMapper mapper = new MonaMapper();

    @Test
    @DisplayName("toDomain mapea CreateMonaRequest a Mona con valores correctos")
    void toDomain_shouldMapRequestToDomain() {
        CreateMonaRequest request = CreateMonaRequest.builder()
                .name("Legendario")
                .description("Insignia legendaria")
                .category(MonaCategory.LEGENDARY)
                .xpReward(500)
                .iconUrl("http://example.com/icon.png")
                .build();

        Mona result = mapper.toDomain(request);

        assertThat(result.getName()).isEqualTo("Legendario");
        assertThat(result.getDescription()).isEqualTo("Insignia legendaria");
        assertThat(result.getCategory()).isEqualTo(MonaCategory.LEGENDARY);
        assertThat(result.getXpReward()).isEqualTo(500);
        assertThat(result.getIconUrl()).isEqualTo("http://example.com/icon.png");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("toDomain mapea request sin iconUrl correctamente")
    void toDomain_shouldHandleNullIconUrl() {
        CreateMonaRequest request = CreateMonaRequest.builder()
                .name("Común")
                .description("Insignia común")
                .category(MonaCategory.COMMON)
                .xpReward(50)
                .build();

        Mona result = mapper.toDomain(request);

        assertThat(result.getIconUrl()).isNull();
        assertThat(result.getName()).isEqualTo("Común");
    }

    @Test
    @DisplayName("toResponse mapea Mona a MonaResponse correctamente")
    void toResponse_shouldMapDomainToResponse() {
        LocalDateTime now = LocalDateTime.now();
        Mona mona = Mona.builder()
                .id("Mona-001")
                .name("Legendario")
                .description("Insignia legendaria")
                .category(MonaCategory.LEGENDARY)
                .xpReward(500)
                .iconUrl("http://example.com/icon.png")
                .createdAt(now)
                .active(true)
                .build();

        MonaResponse result = mapper.toResponse(mona);

        assertThat(result.getId()).isEqualTo("Mona-001");
        assertThat(result.getName()).isEqualTo("Legendario");
        assertThat(result.getDescription()).isEqualTo("Insignia legendaria");
        assertThat(result.getCategory()).isEqualTo(MonaCategory.LEGENDARY);
        assertThat(result.getXpReward()).isEqualTo(500);
        assertThat(result.getIconUrl()).isEqualTo("http://example.com/icon.png");
        assertThat(result.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toResponse omite active field del response")
    void toResponse_shouldOmitActiveField() {
        Mona mona = Mona.builder()
                .id("Mona-002")
                .name("Inactivo")
                .description("Inactiva")
                .category(MonaCategory.RARE)
                .xpReward(200)
                .createdAt(LocalDateTime.now())
                .active(false)
                .build();

        MonaResponse result = mapper.toResponse(mona);

        assertThat(result.getId()).isEqualTo("Mona-002");
    }
}
