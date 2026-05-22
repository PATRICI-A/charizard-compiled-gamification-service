package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardBadgeRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.BadgeNotFoundException;
import com.charizad.compiled.gamification_service.domain.exceptions.InvalidEventCodeException;
import com.charizad.compiled.gamification_service.domain.model.Badge;
import com.charizad.compiled.gamification_service.domain.model.EventCode;
import com.charizad.compiled.gamification_service.domain.model.enums.BadgeCategory;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardBadgeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.BadgeRepositoryPort;
import com.charizad.compiled.gamification_service.domain.ports.out.EventCodeRepositoryPort;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedeemEventCodeServiceTest {

    private static final UUID EC1 = UUID.fromString("30000000-0000-0000-0000-000000000001");
    private static final UUID EC2 = UUID.fromString("30000000-0000-0000-0000-000000000002");
    private static final UUID EC3 = UUID.fromString("30000000-0000-0000-0000-000000000003");
    private static final UUID EC4 = UUID.fromString("30000000-0000-0000-0000-000000000004");
    private static final UUID EC5 = UUID.fromString("30000000-0000-0000-0000-000000000005");
    private static final UUID BADGE_ID = UUID.fromString("00000000-0000-0000-0000-000000000009");

    @Mock private EventCodeRepositoryPort eventCodeRepository;
    @Mock private BadgeRepositoryPort badgeRepository;
    @Mock private AwardBadgeUseCase awardBadgeUseCase;

    @InjectMocks
    private RedeemEventCodeService service;

    private EventCode validCode(UUID id, String code) {
        return EventCode.builder()
                .id(id).code(code)
                .validFrom(LocalDateTime.now().minusHours(1))
                .validUntil(LocalDateTime.now().plusHours(1))
                .usedByUserIds(new ArrayList<>())
                .build();
    }

    private Badge asistenteBadge() {
        return Badge.builder().id(BADGE_ID).name("Asistente")
                .category(BadgeCategory.RARE).active(true).build();
    }

    @Test
    @DisplayName("Código válido y no usado → otorga Asistente")
    void execute_validCode_awardsAsistente() {
        EventCode code = validCode(EC1, "ABC123");
        when(eventCodeRepository.findByCode("ABC123")).thenReturn(Optional.of(code));
        when(badgeRepository.findByName("Asistente")).thenReturn(Optional.of(asistenteBadge()));
        EarnedBadgeResponse response = mock(EarnedBadgeResponse.class);
        when(awardBadgeUseCase.execute(any())).thenReturn(response);

        EarnedBadgeResponse result = service.execute("u1", "ABC123");

        assertThat(result).isSameAs(response);
        verify(eventCodeRepository).save(code);

        ArgumentCaptor<AwardBadgeRequest> captor = ArgumentCaptor.forClass(AwardBadgeRequest.class);
        verify(awardBadgeUseCase).execute(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo("u1");
        assertThat(captor.getValue().getBadgeId()).isEqualTo(BADGE_ID);
    }

    @Test
    @DisplayName("Código no encontrado → InvalidEventCodeException")
    void execute_codeNotFound_throws() {
        when(eventCodeRepository.findByCode("NOPE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("u1", "NOPE"))
                .isInstanceOf(InvalidEventCodeException.class);
    }

    @Test
    @DisplayName("Código expirado → InvalidEventCodeException")
    void execute_expiredCode_throws() {
        EventCode expired = EventCode.builder()
                .id(EC2).code("OLD")
                .validFrom(LocalDateTime.now().minusDays(2))
                .validUntil(LocalDateTime.now().minusDays(1))
                .usedByUserIds(new ArrayList<>())
                .build();
        when(eventCodeRepository.findByCode("OLD")).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> service.execute("u1", "OLD"))
                .isInstanceOf(InvalidEventCodeException.class);
    }

    @Test
    @DisplayName("Código aún no activo → InvalidEventCodeException")
    void execute_notYetActiveCode_throws() {
        EventCode future = EventCode.builder()
                .id(EC3).code("FUTURE")
                .validFrom(LocalDateTime.now().plusHours(1))
                .validUntil(LocalDateTime.now().plusDays(1))
                .usedByUserIds(new ArrayList<>())
                .build();
        when(eventCodeRepository.findByCode("FUTURE")).thenReturn(Optional.of(future));

        assertThatThrownBy(() -> service.execute("u1", "FUTURE"))
                .isInstanceOf(InvalidEventCodeException.class);
    }

    @Test
    @DisplayName("Código ya usado por el mismo usuario → InvalidEventCodeException")
    void execute_alreadyUsedByUser_throws() {
        ArrayList<String> usedBy = new ArrayList<>();
        usedBy.add("u1");
        EventCode used = EventCode.builder()
                .id(EC4).code("USED")
                .validFrom(LocalDateTime.now().minusHours(1))
                .validUntil(LocalDateTime.now().plusHours(1))
                .usedByUserIds(usedBy)
                .build();
        when(eventCodeRepository.findByCode("USED")).thenReturn(Optional.of(used));

        assertThatThrownBy(() -> service.execute("u1", "USED"))
                .isInstanceOf(InvalidEventCodeException.class);
    }

    @Test
    @DisplayName("Badge Asistente no existe en catálogo → BadgeNotFoundException")
    void execute_badgeNotFound_throws() {
        EventCode code = validCode(EC1, "ABC");
        when(eventCodeRepository.findByCode("ABC")).thenReturn(Optional.of(code));
        when(badgeRepository.findByName("Asistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("u1", "ABC"))
                .isInstanceOf(BadgeNotFoundException.class);
    }

    @Test
    @DisplayName("Código válido usado por otro usuario — usuario nuevo puede canjearlo")
    void execute_usedByOtherUser_successForNewUser() {
        ArrayList<String> usedBy = new ArrayList<>();
        usedBy.add("other-user");
        EventCode code = EventCode.builder()
                .id(EC5).code("SHARED")
                .validFrom(LocalDateTime.now().minusHours(1))
                .validUntil(LocalDateTime.now().plusHours(1))
                .usedByUserIds(usedBy)
                .build();
        when(eventCodeRepository.findByCode("SHARED")).thenReturn(Optional.of(code));
        when(badgeRepository.findByName("Asistente")).thenReturn(Optional.of(asistenteBadge()));
        when(awardBadgeUseCase.execute(any())).thenReturn(mock(EarnedBadgeResponse.class));

        service.execute("new-user", "SHARED");

        verify(eventCodeRepository).save(code);
    }
}
