package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.application.mapper.UserGamificationMapper;
import com.charizad.compiled.gamification_service.domain.exceptions.MonaNotFoundException;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.UserGamification;
import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import com.charizad.compiled.gamification_service.domain.ports.in.CheckXpRewardsUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.NotificationEventPort;
import com.charizad.compiled.gamification_service.domain.ports.out.UserGamificationRepositoryPort;
import com.charizad.compiled.gamification_service.domain.valueobjects.EarnedMona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwardMonaServiceTest {

    @Mock private MonaRepositoryPort MonaRepository;
    @Mock private UserGamificationRepositoryPort userGamificationRepository;
    @Mock private NotificationEventPort notificationEventPort;
    @Mock private UserGamificationMapper userGamificationMapper;
    @Mock private CheckXpRewardsUseCase checkXpRewardsUseCase;

    @InjectMocks
    private AwardMonaService service;

    private Mona Mona;
    private AwardMonaRequest request;

    @BeforeEach
    void setUp() {
        Mona = Mona.builder()
                .id("Mona-001")
                .name("Primer Parche")
                .description("Asististe a tu primer parche")
                .category(MonaCategory.COMMON)
                .xpReward(100)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        request = AwardMonaRequest.builder()
                .userId("user-001")
                .monaId("Mona-001")
                .build();
    }

    /*@Test
    @DisplayName("Otorgar insignia a usuario nuevo crea perfil y suma XP")
    void execute_shouldCreateProfileAndAwardMona_whenUserDoesNotExist() {
        when(MonaRepository.findById("Mona-001")).thenReturn(Optional.of(Mona));
        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.empty());
        when(userGamificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userGamificationMapper.toEarnedMonaResponse(any())).thenReturn(
                EarnedMonaResponse.builder().monaId("Mona-001").xpAwarded(100).build());

        EarnedMonaResponse response = service.execute(request);

        assertThat(response.getMonaId()).isEqualTo("Mona-001");
        assertThat(response.getXpAwarded()).isEqualTo(100);
        verify(userGamificationRepository).save(any());
        verify(notificationEventPort).notifyMonaEarned("user-001", Mona);
    }*/

    @Test
    @DisplayName("Otorgar insignia ya poseída retorna la existente sin lanzar error (E1)")
    void execute_shouldReturnExistingMona_whenUserAlreadyHasIt() {
        EarnedMona alreadyOwned = EarnedMona.builder()
                .monaId("Mona-001")
                .monaName("Primer Parche")
                .earnedAt(LocalDateTime.now().minusDays(5))
                .xpAwarded(100)
                .build();

        ArrayList<EarnedMona> earnedList = new ArrayList<>();
        earnedList.add(alreadyOwned);

        UserGamification userWithMona = UserGamification.builder()
                .id("ug-001")
                .userId("user-001")
                .totalXp(100)
                .weeklyXp(100)
                .rankingOptIn(false)
                .earnedMonas(earnedList)
                .progress(new ArrayList<>())
                .build();

        when(MonaRepository.findById("Mona-001")).thenReturn(Optional.of(Mona));
        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.of(userWithMona));
        when(userGamificationMapper.toEarnedMonaResponse(alreadyOwned)).thenReturn(
                EarnedMonaResponse.builder().monaId("Mona-001").xpAwarded(100).build());

        EarnedMonaResponse response = service.execute(request);

        assertThat(response.getMonaId()).isEqualTo("Mona-001");
        verify(userGamificationRepository, never()).save(any());
        verify(notificationEventPort, never()).notifyMonaEarned(any(), any());
    }

    @Test
    @DisplayName("Lanza MonaNotFoundException si la insignia no existe")
    void execute_shouldThrow_whenMonaNotFound() {
        when(MonaRepository.findById("Mona-001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(request))
                .isInstanceOf(MonaNotFoundException.class)
                .hasMessageContaining("Mona-001");

        verifyNoInteractions(userGamificationRepository);
        verifyNoInteractions(notificationEventPort);
    }

    @Test
    @DisplayName("Guarda el usuario con el XP correcto después de otorgar la insignia")
    void execute_shouldSaveUserWithCorrectXp() {
        when(MonaRepository.findById("Mona-001")).thenReturn(Optional.of(Mona));
        when(userGamificationRepository.findByUserId("user-001")).thenReturn(Optional.empty());
        when(userGamificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userGamificationMapper.toEarnedMonaResponse(any())).thenReturn(new EarnedMonaResponse());

        service.execute(request);

        ArgumentCaptor<UserGamification> captor = ArgumentCaptor.forClass(UserGamification.class);
        verify(userGamificationRepository).save(captor.capture());

        UserGamification saved = captor.getValue();
        assertThat(saved.getTotalXp()).isEqualTo(100);
        assertThat(saved.getWeeklyXp()).isEqualTo(100);
        assertThat(saved.getEarnedMonas()).hasSize(1);
    }
}
