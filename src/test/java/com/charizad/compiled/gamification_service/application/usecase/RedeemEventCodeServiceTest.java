package com.charizad.compiled.gamification_service.application.usecase;

import com.charizad.compiled.gamification_service.application.dto.request.AwardMonaRequest;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedMonaResponse;
import com.charizad.compiled.gamification_service.domain.exceptions.MonaNotFoundException;
import com.charizad.compiled.gamification_service.domain.exceptions.InvalidEventCodeException;
import com.charizad.compiled.gamification_service.domain.model.Mona;
import com.charizad.compiled.gamification_service.domain.model.EventCode;
import com.charizad.compiled.gamification_service.domain.model.enums.MonaCategory;
import com.charizad.compiled.gamification_service.domain.ports.in.AwardMonaUseCase;
import com.charizad.compiled.gamification_service.domain.ports.out.MonaRepositoryPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedeemEventCodeServiceTest {

    @Mock private EventCodeRepositoryPort eventCodeRepository;
    @Mock private MonaRepositoryPort MonaRepository;
    @Mock private AwardMonaUseCase awardMonaUseCase;

    @InjectMocks
    private RedeemEventCodeService service;

    private EventCode validCode(String code) {
        return EventCode.builder()
                .id("ec1").code(code)
                .validFrom(LocalDateTime.now().minusHours(1))
                .validUntil(LocalDateTime.now().plusHours(1))
                .usedByUserIds(new ArrayList<>())
                .build();
    }

    private Mona asistenteMona() {
        return Mona.builder().id("b-asistente").name("Asistente")
                .category(MonaCategory.RARE).active(true).build();
    }

    @Test
    @DisplayName("Código válido y no usado → otorga Asistente")
    void execute_validCode_awardsAsistente() {
        EventCode code = validCode("ABC123");
        when(eventCodeRepository.findByCode("ABC123")).thenReturn(Optional.of(code));
        when(MonaRepository.findByName("Asistente")).thenReturn(Optional.of(asistenteMona()));
        EarnedMonaResponse response = mock(EarnedMonaResponse.class);
        when(awardMonaUseCase.execute(any())).thenReturn(response);

        EarnedMonaResponse result = service.execute("u1", "ABC123");

        assertThat(result).isSameAs(response);
        verify(eventCodeRepository).save(code);

        ArgumentCaptor<AwardMonaRequest> captor = ArgumentCaptor.forClass(AwardMonaRequest.class);
        verify(awardMonaUseCase).execute(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo("u1");
        assertThat(captor.getValue().getMonaId()).isEqualTo("b-asistente");
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
                .id("ec2").code("OLD")
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
                .id("ec3").code("FUTURE")
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
                .id("ec4").code("USED")
                .validFrom(LocalDateTime.now().minusHours(1))
                .validUntil(LocalDateTime.now().plusHours(1))
                .usedByUserIds(usedBy)
                .build();
        when(eventCodeRepository.findByCode("USED")).thenReturn(Optional.of(used));

        assertThatThrownBy(() -> service.execute("u1", "USED"))
                .isInstanceOf(InvalidEventCodeException.class);
    }

    @Test
    @DisplayName("Mona Asistente no existe en catálogo → MonaNotFoundException")
    void execute_MonaNotFound_throws() {
        EventCode code = validCode("ABC");
        when(eventCodeRepository.findByCode("ABC")).thenReturn(Optional.of(code));
        when(MonaRepository.findByName("Asistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("u1", "ABC"))
                .isInstanceOf(MonaNotFoundException.class);
    }

    @Test
    @DisplayName("Código válido usado por otro usuario — usuario nuevo puede canjearlo")
    void execute_usedByOtherUser_successForNewUser() {
        ArrayList<String> usedBy = new ArrayList<>();
        usedBy.add("other-user");
        EventCode code = EventCode.builder()
                .id("ec5").code("SHARED")
                .validFrom(LocalDateTime.now().minusHours(1))
                .validUntil(LocalDateTime.now().plusHours(1))
                .usedByUserIds(usedBy)
                .build();
        when(eventCodeRepository.findByCode("SHARED")).thenReturn(Optional.of(code));
        when(MonaRepository.findByName("Asistente")).thenReturn(Optional.of(asistenteMona()));
        when(awardMonaUseCase.execute(any())).thenReturn(mock(EarnedMonaResponse.class));

        service.execute("new-user", "SHARED");

        verify(eventCodeRepository).save(code);
    }
}
