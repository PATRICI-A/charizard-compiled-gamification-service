package com.charizad.compiled.gamification_service.application.mapper;

import com.charizad.compiled.gamification_service.application.dto.request.CreateBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.BadgeResponse;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BadgeMapperTest {

    private static final UUID BADGE_ID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID BADGE_ID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final BadgeMapper mapper = new BadgeMapper();

    @Test
    @DisplayName("toDomain mapea CreateBadgeRequest a Badge con valores correctos")
    void toDomain_shouldMapRequestToDomain() {
        CreateBadgeRequest request = CreateBadgeRequest.builder()
                .name("Legendario")
                .description("Insignia legendaria")
                .category(BadgeCategory.LEGENDARY)
                .xpReward(500)
                .iconUrl("http://example.com/icon.png")
                .build();

        Badge result = mapper.toDomain(request);

        assertThat(result.getName()).isEqualTo("Legendario");
        assertThat(result.getDescription()).isEqualTo("Insignia legendaria");
        assertThat(result.getCategory()).isEqualTo(BadgeCategory.LEGENDARY);
        assertThat(result.getXpReward()).isEqualTo(500);
        assertThat(result.getIconUrl()).isEqualTo("http://example.com/icon.png");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("toDomain mapea request sin iconUrl correctamente")
    void toDomain_shouldHandleNullIconUrl() {
        CreateBadgeRequest request = CreateBadgeRequest.builder()
                .name("Común")
                .description("Insignia común")
                .category(BadgeCategory.COMMON)
                .xpReward(50)
                .build();

        Badge result = mapper.toDomain(request);

        assertThat(result.getIconUrl()).isNull();
        assertThat(result.getName()).isEqualTo("Común");
    }

    @Test
    @DisplayName("toResponse mapea Badge a BadgeResponse correctamente")
    void toResponse_shouldMapDomainToResponse() {
        LocalDateTime now = LocalDateTime.now();
        Badge badge = Badge.builder()
                .id(BADGE_ID_1)
                .name("Legendario")
                .description("Insignia legendaria")
                .category(BadgeCategory.LEGENDARY)
                .xpReward(500)
                .iconUrl("http://example.com/icon.png")
                .createdAt(now)
                .active(true)
                .build();

        BadgeResponse result = mapper.toResponse(badge);

        assertThat(result.getId()).isEqualTo(BADGE_ID_1);
        assertThat(result.getName()).isEqualTo("Legendario");
        assertThat(result.getDescription()).isEqualTo("Insignia legendaria");
        assertThat(result.getCategory()).isEqualTo(BadgeCategory.LEGENDARY);
        assertThat(result.getXpReward()).isEqualTo(500);
        assertThat(result.getIconUrl()).isEqualTo("http://example.com/icon.png");
        assertThat(result.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toResponse omite active field del response")
    void toResponse_shouldOmitActiveField() {
        Badge badge = Badge.builder()
                .id(BADGE_ID_2)
                .name("Inactivo")
                .description("Inactiva")
                .category(BadgeCategory.RARE)
                .xpReward(200)
                .createdAt(LocalDateTime.now())
                .active(false)
                .build();

        BadgeResponse result = mapper.toResponse(badge);

        assertThat(result.getId()).isEqualTo(BADGE_ID_2);
    }
}
