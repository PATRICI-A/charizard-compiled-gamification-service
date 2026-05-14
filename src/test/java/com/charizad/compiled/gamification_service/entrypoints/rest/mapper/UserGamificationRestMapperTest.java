package com.charizad.compiled.gamification_service.entrypoints.rest.mapper;

import com.charizad.compiled.gamification_service.application.dto.request.ToggleRankingOptInRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserGamificationRestMapperTest {

    private final UserGamificationRestMapper mapper = new UserGamificationRestMapper();

    @Test
    @DisplayName("toToggleRequest construye ToggleRankingOptInRequest con userId")
    void toToggleRequest_shouldBuildRequest() {
        ToggleRankingOptInRequest result = mapper.toToggleRequest("user-001");

        assertThat(result.getUserId()).isEqualTo("user-001");
    }
}
