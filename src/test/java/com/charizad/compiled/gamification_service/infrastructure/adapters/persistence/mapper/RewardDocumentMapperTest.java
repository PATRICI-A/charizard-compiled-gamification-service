package com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.mapper;

import com.charizad.compiled.gamification_service.domain.model.Reward;
import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import com.charizad.compiled.gamification_service.infrastructure.adapters.persistence.entity.RewardDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RewardDocumentMapperTest {

    private final RewardDocumentMapper mapper = new RewardDocumentMapper();
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("toDomain mapea todos los campos correctamente")
    void toDomain_mapsAllFields() {
        RewardDocument doc = RewardDocument.builder()
                .id("r1")
                .name("Reward One")
                .description("desc")
                .type(RewardType.TITLE)
                .xpThreshold(200)
                .iconUrl("http://icon.url")
                .createdAt(now)
                .active(true)
                .build();

        Reward result = mapper.toDomain(doc);

        assertThat(result.getId()).isEqualTo("r1");
        assertThat(result.getName()).isEqualTo("Reward One");
        assertThat(result.getDescription()).isEqualTo("desc");
        assertThat(result.getType()).isEqualTo(RewardType.TITLE);
        assertThat(result.getXpThreshold()).isEqualTo(200);
        assertThat(result.getIconUrl()).isEqualTo("http://icon.url");
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("toDocument mapea todos los campos correctamente")
    void toDocument_mapsAllFields() {
        Reward domain = Reward.builder()
                .id("r2")
                .name("Reward Two")
                .description("desc2")
                .type(RewardType.TITLE)
                .xpThreshold(500)
                .iconUrl("http://icon2.url")
                .createdAt(now)
                .active(false)
                .build();

        RewardDocument result = mapper.toDocument(domain);

        assertThat(result.getId()).isEqualTo("r2");
        assertThat(result.getName()).isEqualTo("Reward Two");
        assertThat(result.getDescription()).isEqualTo("desc2");
        assertThat(result.getType()).isEqualTo(RewardType.TITLE);
        assertThat(result.getXpThreshold()).isEqualTo(500);
        assertThat(result.getIconUrl()).isEqualTo("http://icon2.url");
        assertThat(result.getCreatedAt()).isEqualTo(now);
        assertThat(result.isActive()).isFalse();
    }

    @Test
    @DisplayName("toDomain maneja campos nulos sin lanzar excepción")
    void toDomain_nullFields_noException() {
        RewardDocument doc = RewardDocument.builder().id("r3").build();

        Reward result = mapper.toDomain(doc);

        assertThat(result.getId()).isEqualTo("r3");
        assertThat(result.getName()).isNull();
    }

    @Test
    @DisplayName("toDocument maneja campos nulos sin lanzar excepción")
    void toDocument_nullFields_noException() {
        Reward domain = Reward.builder().id("r4").build();

        RewardDocument result = mapper.toDocument(domain);

        assertThat(result.getId()).isEqualTo("r4");
        assertThat(result.getName()).isNull();
    }
}
