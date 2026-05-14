package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.CreateRewardRequest;
import com.charizad.compiled.gamification_service.application.dto.response.RewardResponse;
import com.charizad.compiled.gamification_service.domain.model.Reward;
import com.charizad.compiled.gamification_service.domain.model.enums.RewardType;
import com.charizad.compiled.gamification_service.domain.ports.out.RewardRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRewardServiceTest {

    @Mock private RewardRepositoryPort rewardRepository;

    @InjectMocks
    private CreateRewardService service;

    @Captor
    private ArgumentCaptor<Reward> rewardCaptor;

    @Test
    @DisplayName("Crea un reward y retorna la respuesta")
    void execute_shouldCreateAndReturnReward() {
        CreateRewardRequest request = CreateRewardRequest.builder()
                .name("Gold Title")
                .description("Un título dorado")
                .type(RewardType.TITLE)
                .xpThreshold(500)
                .iconUrl("https://example.com/gold.png")
                .build();

        Reward saved = Reward.builder()
                .id("reward-001")
                .name("Gold Title")
                .description("Un título dorado")
                .type(RewardType.TITLE)
                .xpThreshold(500)
                .iconUrl("https://example.com/gold.png")
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        when(rewardRepository.save(any())).thenReturn(saved);

        RewardResponse result = service.execute(request);

        assertThat(result.getId()).isEqualTo("reward-001");
        assertThat(result.getName()).isEqualTo("Gold Title");
        assertThat(result.getDescription()).isEqualTo("Un título dorado");
        assertThat(result.getType()).isEqualTo(RewardType.TITLE);
        assertThat(result.getXpThreshold()).isEqualTo(500);
        assertThat(result.getIconUrl()).isEqualTo("https://example.com/gold.png");
        assertThat(result.isActive()).isTrue();
        assertThat(result.getCreatedAt()).isNotNull();

        verify(rewardRepository).save(rewardCaptor.capture());
        Reward captured = rewardCaptor.getValue();
        assertThat(captured.getName()).isEqualTo("Gold Title");
        assertThat(captured.getType()).isEqualTo(RewardType.TITLE);
        assertThat(captured.getXpThreshold()).isEqualTo(500);
        assertThat(captured.isActive()).isTrue();
    }
}
