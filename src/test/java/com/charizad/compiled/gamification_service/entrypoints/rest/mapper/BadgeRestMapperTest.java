package com.charizad.compiled.gamification_service.entrypoints.rest.mapper;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.request.CreateBadgeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BadgeRestMapperTest {

    private final BadgeRestMapper mapper = new BadgeRestMapper();

    @Test
    @DisplayName("toCreateRequest retorna el mismo objeto recibido")
    void toCreateRequest_shouldReturnSameObject() {
        CreateBadgeRequest request = CreateBadgeRequest.builder()
                .name("Test")
                .description("Desc")
                .build();

        CreateBadgeRequest result = mapper.toCreateRequest(request);

        assertThat(result).isSameAs(request);
        assertThat(result.getName()).isEqualTo("Test");
    }

    @Test
    @DisplayName("toAwardRequest construye AwardBadgeRequest correctamente")
    void toAwardRequest_shouldBuildRequest() {
        AwardBadgeRequest result = mapper.toAwardRequest("user-001", "badge-001");

        assertThat(result.getUserId()).isEqualTo("user-001");
        assertThat(result.getBadgeId()).isEqualTo("badge-001");
    }
}
